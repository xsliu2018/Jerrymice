del /q bootstrap.jar
jar cvf0 bootstrap.jar -C out/production/Jerrymice cn/java/jerrymice/Bootstrap.class -C out/production/Jerrymice cn/java/jerrymice/classloader/CommonClassLoader.class
del /q lib/jerrymice.jar
cd out
cd production
cd jerrymice
jar cvf0 ../../../lib/jerrymice.jar *
cd ..
cd ..
cd ..
java -cp bootstrap.jar cn.java.jerrymice.Bootstrap
pause