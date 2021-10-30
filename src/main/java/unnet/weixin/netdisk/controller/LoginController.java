package unnet.weixin.netdisk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import unnet.weixin.netdisk.base.BaseController;
import unnet.weixin.netdisk.entity.*;
import unnet.weixin.netdisk.services.CreateBucketService;
import unnet.weixin.netdisk.services.DepartmentInfoService;
import unnet.weixin.netdisk.services.MyUserService;
import unnet.weixin.netdisk.services.UserInfoService;
import unnet.weixin.netdisk.utils.AesCbcUtil;
import unnet.weixin.netdisk.utils.Gensign;
import unnet.weixin.netdisk.utils.HttpsUtils;
import unnet.weixin.netdisk.utils.TokenSingleton;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author 蔡可成
 * @DateTime 2020/9/9
 */
@Controller
@CrossOrigin(maxAge = 3600, allowCredentials = "true")
@RequestMapping("/login")
public class LoginController extends BaseController {
    @Resource
    private WechatInfo wechatInfo;

    @Resource
    private MyUserService myUserService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private CreateBucketService createBucketService;

    @Resource
    private TokenSingleton tokenSingleton;

    @Resource
    private DepartmentInfoService departmentInfoService;


    @Resource
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

//    @ApiOperation("登录")
//    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
//    public void getCode(@RequestParam("code") String code) throws Exception {
//        String access_token = tokenSingleton.getAccessToken(wechatInfo);
//        JSONObject result2 = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getUserInfoUrl() +
//                access_token + "&code=" + code, null));
//        String userId = result2.get("UserId").toString();
//        String bucket = userId.toLowerCase();
//        if (bucket.contains("_")) {
//            bucket = bucket.replace("_", "");
//        }
//        if (bucket.contains("@")) {
//            bucket = bucket.replace("@", "");
//        }
//        if (bucket.contains(".")) {
//            bucket = bucket.replace(".", "");
//        }
//        UserInfo user = userInfoService.selectUserByUserId(userId);
////       新用户
//        if (user == null) {
//            user = (UserInfo) getInfo(userId).getData();
//            logger.warn(user);
//            user.setBucket(bucket);
//            int i = userInfoService.insertUserInfo(user);
//            if (i == 1) {
//                logger.warn("用户数据插入成功");
//                createBucketService.createBucket(bucket);
//            }
//        }
//        session.setAttribute("userInfo", user);
//        session.setAttribute("bucket", bucket);
//        String userName = user.getName();
//        Subject currentUser = SecurityUtils.getSubject();
//        logger.info("-----开始 shiro鉴权");
//        if (!currentUser.isAuthenticated()) {
//            logger.info("-----进入 shiro鉴权");
//            UsernamePasswordToken token = new UsernamePasswordToken(userName, userId);
//            try {
//                currentUser.login(token);
//                logger.info("-----shiro success");
//            } catch (Exception e) {
//                logger.warn(e.getMessage());
//            }
//        }
//        response.sendRedirect(wechatInfo.getRedirectUrl() + access_token + "&userId=" + userId);
//    }
//
//    @ApiOperation("获取用户信息")
//    @ResponseBody
//    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
//    public RestResult<?> getInfo(@RequestParam("userId") String userId) {
//        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
//        if (userInfo == null) {
//            String access_token = tokenSingleton.getAccessToken(wechatInfo);
//            JSONObject result = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getUserDetailUrl() +
//                    access_token + "&userid=" + userId, null));
//            userInfo = JSONObject.parseObject(result.toJSONString(), UserInfo.class);
//        }
//        logger.warn("用户信息" + userInfo);
//        return new RestResult<>(0, "用户信息", userInfo);
//    }

//    @ApiOperation("获取部门列表")
//    @ResponseBody
//    @RequestMapping(value = "/getDepartment", method = RequestMethod.GET)
//    public RestResult<?> getDepartment() {
//        List<DepartmentInfo> list = departmentInfoService.list();
//        if (list != null && list.size() > 0) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("errcode", 0);
//            map.put("errmsg", "ok");
//            map.put("department", list);
//            logger.warn("部门列表" + map);
//            return new RestResult<>(0, "部门列表", map);
//        } else {
//            String access_token = tokenSingleton.getAccessToken(wechatInfo);
//            JSONObject result = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getDepartmentListUrl() + access_token, null));
//            logger.warn("部门列表" + result);
//            return new RestResult<>(0, "部门列表", result);
//        }
//    }
//
//    @ApiOperation("应用签名信息")
//    @ResponseBody
//    @RequestMapping(value = "/getAppSign", method = RequestMethod.GET)
//    public RestResult<?> getWxAppConfig(String url) {
//        String access_token = tokenSingleton.getAccessToken(wechatInfo);
//        Map<String, String> configMap = new HashMap<>(8);
//        String timestamp = Long.toString(System.currentTimeMillis() / 1000); // 必填，生成签名的时间戳
//        String nonceStr = UUID.randomUUID().toString(); // 必填，生成签名的随机串
//        JSONObject urlStr = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getTicket() + access_token + "&type=agent_config", null));
//        String jsapiTicket = urlStr.get("ticket").toString();
//        configMap.put("appid", wechatInfo.getCorpID());
//        configMap.put("agentid", wechatInfo.getAgentID());
//        configMap.put("noncestr", nonceStr);
//        configMap.put("jsapi_ticket", jsapiTicket);
//        configMap.put("timestamp", timestamp);
//        configMap.put("url", url);
//        String sign = Gensign.gengeSign(configMap);
//        configMap.put("signature", sign);
//        configMap.remove("noncestr");
//        configMap.put("nonceStr", nonceStr);
//        return new RestResult<>(0, "应用签名信息", configMap);
//    }
//
//    @ApiOperation("企业签名信息")
//    @ResponseBody
//    @RequestMapping(value = "/getCorpSign", method = RequestMethod.GET)
//    public RestResult<?> getWxCorConfig(String url) {
//        TokenSingleton instance = TokenSingleton.getInstance();
//        String access_token = instance.getAccessToken(wechatInfo);
//        Map<String, String> configMap = new HashMap<>(8);
//        String timestamp = Long.toString(System.currentTimeMillis() / 1000); // 必填，生成签名的时间戳
//        String nonceStr = UUID.randomUUID().toString(); // 必填，生成签名的随机串
//        JSONObject urlStr = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getJsapiTicket() + access_token, null));
//        String jsapiTicket = urlStr.get("ticket").toString();
//        configMap.put("noncestr", nonceStr);
//        configMap.put("jsapi_ticket", jsapiTicket);
//        configMap.put("timestamp", timestamp);
//        configMap.put("url", url);
//        String sign = Gensign.gengeSign(configMap);
//        configMap.put("signature", sign);
//        configMap.put("appid", wechatInfo.getCorpID());
//        configMap.remove("noncestr");
//        configMap.put("nonceStr", nonceStr);
//        return new RestResult<>(0, "企业签名信息", configMap);
//    }

//    @ApiOperation("线下登录接口")
//    @ResponseBody
//    @PostMapping("/selfLogin")
//    public RestResult<?> login(@RequestParam("userName") String userName, @RequestParam("userId") String userId) {
//        Subject currentUser = SecurityUtils.getSubject();
//        UserInfo user = userInfoService.selectUserByUserId(userId);
//        session.setAttribute("userInfo", user);
//        session.setAttribute("bucket", user.getBucket());
//        logger.info("-----开始 shiro鉴权");
//        if (!currentUser.isAuthenticated()) {
//            logger.info("-----进入 shiro鉴权");
//            UsernamePasswordToken token = new UsernamePasswordToken(userName, userId);
//            try {
//                currentUser.login(token);
//                logger.info("-----shiro success");
//            } catch (Exception e) {
//                logger.warn(e.getMessage());
//                return new RestResult<>(0, "登录失败", null);
//            }
//        }
//        return new RestResult<>(0, "登录成功", null);
//    }
//
//    /**
//     * 定期更新用户信息-----每天23:00更新
//     */
//    @Scheduled(cron = "0 0 23 * * ?")
//    public void updateUserInfo() {
//        List<UserInfo> userInfos = userInfoService.list();
//        String access_token = tokenSingleton.getAccessToken(wechatInfo);
//        for (UserInfo userInfo : userInfos) {
//            JSONObject result = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getUserDetailUrl() +
//                    access_token + "&userid=" + userInfo.getUserid(), null));
//            userInfo = JSONObject.parseObject(result.toJSONString(), UserInfo.class);
//            logger.warn("更新用户信息----" + userInfo);
//            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//            wrapper.eq("userid", userInfo.getUserid());
//            userInfoService.update(userInfo, wrapper);
////            Long aLong = userInfoService.countUsage(userInfo.getUserid());
////            userInfoService.updateUserUsage(userInfo.getUserid(), aLong);
//        }
//    }
//
//    /**
//     * 定期更新部门信息-----每天23:30更新
//     */
//    @Scheduled(cron = "0 30 23 * * ?")
//    public void updateDepartmentInfo() {
//        String access_token = tokenSingleton.getAccessToken(wechatInfo);
//        JSONObject result = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getDepartmentListUrl() + access_token, null));
//        if (result != null && "ok".equals(result.get("errmsg"))) {
//            JSONArray departments = JSONObject.parseArray(result.get("department").toString());
//            if (departments != null) {
//                if (departments.size() > 0) {
//                    for (int i = 0; i < departments.size(); i++) {
//                        JSONObject department = departments.getJSONObject(i);   // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//                        DepartmentInfo departmentInfo = JSONObject.parseObject(department.toJSONString(), DepartmentInfo.class);
//                        departmentInfo.setOrders(department.getString("order"));
//                        logger.warn("更新部门信息----" + departmentInfo);
//                        QueryWrapper<DepartmentInfo> wrapper = new QueryWrapper<>();
//                        wrapper.eq("id", departmentInfo.getId());
//                        departmentInfoService.saveOrUpdate(departmentInfo, wrapper);
//                    }
//                }
//            }
//        } else {
//            logger.warn("部门更新失败");
//        }
//    }

    @ApiOperation("获取用户信息")
    @ResponseBody
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public RestResult<?> getInfo(@RequestParam("encryptedData") String encryptedData, @RequestParam("iv") String iv, @RequestParam("session_key") String session_key, String openid) {
        Map map = new HashMap();
        try {
            String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
            if (null != result && result.length() > 0) {
                map.put("status", 1);
                map.put("msg", "解密成功");
                MyUser myUser = JSON.parseObject(result, MyUser.class);
                myUser.setOpenid(openid);
                String bucket = openid.toLowerCase();
                if (bucket.contains("_")) {
                    bucket = bucket.replace("_", "");
                }
                if (bucket.contains("@")) {
                    bucket = bucket.replace("@", "");
                }
                if (bucket.contains(".")) {
                    bucket = bucket.replace(".", "");
                }
                myUser.setBucket(bucket);
                map.put("userInfo", myUser);
            } else {
                map.put("status", 0);
                map.put("msg", "解密失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RestResult<>(0, (String) map.get("msg"), map.get("userInfo"));
    }


    /**
     * code2session
     */
    @ApiOperation("获取登录态session")
    @GetMapping("/code2session")
    @ResponseBody
    public RestResult<?> getOpenId(@RequestParam("code") String code, @RequestParam("encryptedData") String encryptedData, @RequestParam("iv") String iv) throws Exception {
       JSONObject result = JSON.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getCode2sessionUrl()+wechatInfo.getAppId()+"&secret="+wechatInfo.getSecret()+"&js_code="+code+"&grant_type=authorization_code", null));
//       用户唯一标识符
       String openid = result.getString("openid");
       String sk = result.getString("session_key");
       if(result.get("errcode") != null) {
           return  new RestResult<>(1, result.getString("errmsg"));
       }
       String userSession = DigestUtils.md5DigestAsHex((openid + sk).getBytes());
       LoginStatusInfo loginStatusInfo = new LoginStatusInfo(openid, sk);
       serializableRedisTemplate.opsForValue().set(userSession, loginStatusInfo);

//       用户判断
        MyUser myUser = myUserService.selectMyUserByOpenId(openid);
        MyUser curUser = (MyUser) getInfo(encryptedData, iv, sk, openid).getData();
        System.out.println(curUser);
        session.setAttribute("userInfo", myUser);
        if(myUser == null) {
            int i = myUserService.insertMyUser(curUser);
            if(i == 1) {
                System.out.println("用户插入成功");
                boolean res = createBucketService.createBucket(curUser.getBucket());
                if(res) {
                    System.out.println("创建bucket成功");
                }else {
                    System.out.println("创建bucket失败");
                }
            }
        }
       return  new RestResult<>(0, "请求成功", userSession);
    }

}

