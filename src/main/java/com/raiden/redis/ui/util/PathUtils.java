package com.raiden.redis.ui.util;

import com.raiden.redis.ui.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:28 2022/5/16
 * @Modified By:
 */
public final class PathUtils {

    public static final Logger LOGGER = LogManager.getLogger(PathUtils.class);

    private static final String UTF_8 = "UTF-8";

    private static final String TARGET = File.separator + "target" + File.separator;

    /**
     * 获取项目加载类的根路径
     * @return
     */
    public static String getRootPath(){
        String path = StringUtils.EMPTY;
        try{
            //jar 中没有目录的概念
            URL location = PathUtils.class.getProtectionDomain().getCodeSource().getLocation();//获得当前的URL
            File file = new File(location.getPath());//构建指向当前URL的文件描述符
            if(file.isDirectory()){//如果是目录,指向的是包所在路径，而不是文件所在路径
                path = file.getAbsolutePath();//直接返回绝对路径
            }else{//如果是文件,这个文件指定的是jar所在的路径(注意如果是作为依赖包，这个路径是jvm启动加载的jar文件名)
                path = file.getParent();//返回jar所在的父路径
            }
            path = java.net.URLDecoder.decode(path, UTF_8);
            int firstIndex = path.lastIndexOf(File.pathSeparatorChar) + 1;
            int lastIndex = path.indexOf(TARGET + "classes");
            //如果 lastIndex 等于 0 说明不包含 /target/classes
            if (lastIndex == 0){
                lastIndex = path.indexOf(TARGET + "test-classes");
            }
            if (lastIndex != 0){
                path = path.substring(firstIndex, lastIndex);
            }else {
                path = path.substring(firstIndex);
            }
            LOGGER.info("project path={}", path);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("{}",e);
        }
        return path;
    }
}
