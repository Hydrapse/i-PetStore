package org.csu.ipetstore.component;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by Enzo Cotter on 2020/1/23.
 */
public class MyLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {

        String lang = httpServletRequest.getParameter("language");
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(lang)) {
            String[] split = lang.split("_");
            //第一个参数为语言代码 第二个参数为国家代码
            locale = new Locale(split[0], split[1]);
//            System.out.println("手动切换语言");
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                          Locale locale) {

    }
}
