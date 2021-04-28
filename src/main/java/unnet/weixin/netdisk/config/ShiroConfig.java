package unnet.weixin.netdisk.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unnet.weixin.netdisk.utils.MyFormAuthorizationFilter;

@Configuration
public class ShiroConfig {

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
//		shiroFilterFactoryBean.setLoginUrl("/approval/notLogin");
//		shiroFilterFactoryBean.setUnauthorizedUrl("/approval/notLogin");
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/login/code2session", "anon");
		filterChainDefinitionMap.put("/login/getCode", "anon");
		filterChainDefinitionMap.put("/login/selfLogin", "anon");
		filterChainDefinitionMap.put("/durid", "anon");
		filterChainDefinitionMap.put("/durid/**", "anon");
		filterChainDefinitionMap.put("/image/404.png", "anon"); // 无权限 显示图标 无需授权
		
		//swagger资源文件配置无需鉴权
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/v2/**", "anon");
		filterChainDefinitionMap.put("/swagger-resources/**", "anon");

		// 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
		filterChainDefinitionMap.put("/**", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		//option跨域问题设置
		shiroFilterFactoryBean.getFilters().put("authc", new MyFormAuthorizationFilter());
		
		return shiroFilterFactoryBean;

	}

	@Bean
	public SecurityManager securityManager(CustomRealm customRealm) {
		DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
		defaultSecurityManager.setRealm(customRealm);
		return defaultSecurityManager;
	}

	@Bean
	public CustomRealm customRealm() {
		return new CustomRealm();
	}
}
