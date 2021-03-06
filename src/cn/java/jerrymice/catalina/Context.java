package cn.java.jerrymice.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.java.jerrymice.classloader.CommonClassLoader;
import cn.java.jerrymice.classloader.WebappClassLoader;
import cn.java.jerrymice.exception.WebConfigDuplicatedException;
import cn.java.jerrymice.http.ApplicationContext;
import cn.java.jerrymice.util.ContextXmlUtil;
import cn.java.jerrymice.watcher.ContextFileChangeWatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.jsoup.nodes.Document;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ：xiaosong
 * @description：
 * 代表一个应用，它有两个属性，一个是path，表示访问的路径，docBase表示在文件系统中得分位置
 * @date ：2020/8/4 16:09
 */
public class Context {
    private String path;
    private String docBase;
    private File contextWebXmlFile;
    private Host host;
    private boolean reloadable;
    //存放地址和对应的Servlet的类名
    private Map<String, String> url_servletClassName;
    //存放地址和对应的Servlet的名称
    private Map<String, String> url_servletName;
    // 存放Servlet的名称和其对应的类名
    private Map<String, String> servletName_className;
    // 存放Servlet的类名和其对应的名称
    private Map<String, String> className_servletName;
    // 类加载器
    private WebappClassLoader webappClassLoader;
    // 添加监听器
    private ContextFileChangeWatcher watcher;
    // 添加ServletContext
    private ServletContext servletContext;
    // 用来存储这个Context对应的Servlet,主要是为了让Servlet为单例，
    // 在访问到达时，通过getServlet方法，查看是否已经存在这个类的实例存在，存在直接返回，不存在则创建
    private Map<Class<?>, HttpServlet> servletPool;

    /**
     * Context的构造方法
     * @param path
     * @param docBase
     */
    public Context(String path, String docBase, Host host, boolean reloadable){
        this.path = path;
        this.docBase = docBase;
        this.reloadable = reloadable;
        this.host = host;
        this.servletContext = new ApplicationContext(this);
        // WEB-INF/web.xml
        this.contextWebXmlFile = new File(docBase, ContextXmlUtil.getWatchedResource());
        this.url_servletName = new HashMap<>();
        this.url_servletClassName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);
        this.servletPool = new HashMap<>();
        deploy();

    }

    private void init(){
        if (!contextWebXmlFile.exists()){
            // 如果配置文件不存在
            return;
        }
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document document = Jsoup.parse(xml);
        try{
            checkDuplicated(document);
        }catch (WebConfigDuplicatedException e) {
            LogFactory.get().info(e.toString());
            return;
        }
        parseServletMapping(document);
    }

    public WebappClassLoader getWebappClassLoader(){
        return webappClassLoader;
    }
    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    /**
     * 从Document对象中解析出映射信息
     * @param document: config文件中context.xml中指定的watchedSourceFile
     */
    private void parseServletMapping(Document document){

        // 获取url和ServletName之间的映射
        Elements mappingElements = document.select("servlet-mapping url-pattern");

        for (Element element: mappingElements){
            // 获取这个Servlet对应的url
            String urlPattern = element.text();
            // 获取这个Servlet对应的name
            String servletName = element.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }

        // 获取servletName和className之间的映射
        Elements servletNameElements = document.select("servlet servlet-name");

        for(Element element : servletNameElements){
            String servletName = element.text();
            String className = element.parent().select("servlet-class").first().text();
            className_servletName.put(className, servletName);
            servletName_className.put(servletName, className);
        }

        // 获取url和className之间的映射信息
        // 先将所有的url拿出来
        Set<String> urls = url_servletName.keySet();
        for (String url: urls){
            // 获取url对应的servletName
            String servletName = url_servletName.get(url);
            // 根据servletName获取className
            String className = servletName_className.get(servletName);
            url_servletClassName.put(url, className);
        }
    }

    /**
     * 通过检查xml文件中的映射是否有重复
     * @param document： 从web.xml文件中解析获得的doc对象
     * @param mapping: 想要检查的元素映射，例如 servlet servlet-name
     * @param msg: 反馈的重复信息提示
     * @throws WebConfigDuplicatedException
     */
    private void checkDuplicated(Document document, String mapping, String msg) throws WebConfigDuplicatedException {
        Elements elements = document.select(mapping);
        // 判断的逻辑是先将元素提取出来，放到Set中，从而判断是否重复
        Set<String> elementText = new HashSet<>();
        for(Element element: elements){
            String text = element.text();
            boolean b = elementText.add(text);
            if (!b){
                // 说明其中已经有这个元素了，即重复，抛出异常
                throw new WebConfigDuplicatedException(StrUtil.format(msg, text));
            }
        }
    }

    /**
     * 检查uri， servletName和className是否重复
     * @throws WebConfigDuplicatedException
     */
    private void checkDuplicated(Document document) throws WebConfigDuplicatedException{
        checkDuplicated(document, "servlet-mapping url-pattern","servlet url重复，请保持其唯一性：{}");
        checkDuplicated(document, "servlet servlet-name","servlet名称重复，请保持其唯一性:{}");
        checkDuplicated(document, "servlet servlet-class", "servlet类名重复，请保持其唯一性:{}");
    }


    /**
     * 获取uri对应的className
     * @param uri
     * @return
     */
    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }

    /**
     * 通过uri获取servletName
     * @param uri
     * @return
     */
    public String getServletName(String uri) {
        return url_servletName.get(uri);
    }


    private void deploy(){
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.getDocBase());
        init();
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                this.getDocBase(), timeInterval.intervalMs());
        if (reloadable){
            watcher = new ContextFileChangeWatcher(this);
            watcher.start();
        }
    }
    public void stop(){
        webappClassLoader.stop();
        watcher.stop();
    }

    /**
     * 提供重载方法，当watcher监听到docBase中内容发生了变化时，调用这个方法将对象进行重载
     */
    public void reload(){

    }

    public File getContextWebXmlFile() {
        return contextWebXmlFile;
    }

    public void setContextWebXmlFile(File contextWebXmlFile) {
        this.contextWebXmlFile = contextWebXmlFile;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public Map<String, String> getUrl_servletClassName() {
        return url_servletClassName;
    }

    public void setUrl_servletClassName(Map<String, String> url_servletClassName) {
        this.url_servletClassName = url_servletClassName;
    }

    public Map<String, String> getUrl_servletName() {
        return url_servletName;
    }

    public void setUrl_servletName(Map<String, String> url_servletName) {
        this.url_servletName = url_servletName;
    }

    public Map<String, String> getServletName_className() {
        return servletName_className;
    }

    public void setServletName_className(Map<String, String> servletName_className) {
        this.servletName_className = servletName_className;
    }

    public Map<String, String> getClassName_servletName() {
        return className_servletName;
    }

    public void setClassName_servletName(Map<String, String> className_servletName) {
        this.className_servletName = className_servletName;
    }

    public void setWebappClassLoader(WebappClassLoader webappClassLoader) {
        this.webappClassLoader = webappClassLoader;
    }

    public ContextFileChangeWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(ContextFileChangeWatcher watcher) {
        this.watcher = watcher;
    }

    public ServletContext getServletContext(){
        return this.servletContext;
    }

    public synchronized HttpServlet getServlet(Class<?> clazz) throws InstantiationException, IllegalAccessException,
            ServletException {
        HttpServlet servlet = servletPool.get(clazz);
        if (servlet == null){
            servlet = (HttpServlet) clazz.newInstance();
            servletPool.put(clazz, servlet);
        }
        return servlet;
    }
}
