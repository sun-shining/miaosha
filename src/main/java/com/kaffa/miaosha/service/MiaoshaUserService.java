package com.kaffa.miaosha.service;

import com.kaffa.miaosha.dao.UserDao;
import com.kaffa.miaosha.domain.LoginVO;
import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.exception.GlobalException;
import com.kaffa.miaosha.redis.MiaoShaUserKey;
import com.kaffa.miaosha.redis.RedisService;
import com.kaffa.miaosha.result.CodeMsg;
import com.kaffa.miaosha.utils.MD5Util;
import com.kaffa.miaosha.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/1/17.
 */
@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    /**
     * 对象级缓存,永不过期
     * @param id
     * @return
     */
    public MiaoshaUser getUserById(Long id){
        MiaoshaUser user = redisService.get(MiaoShaUserKey.getById, ""+id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }

        user = userDao.getUserById(id);
        if (user!=null){
            redisService.set(MiaoShaUserKey.getById, ""+id, user);
        }
        return user;
    }

    public boolean login(HttpServletResponse response, LoginVO loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getUserById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPass2DBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //往页面上添加cookie,第一次生成,以后在其他页面用到cookie的时候,更新一下cookie时间就可以
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);

        return true;
    }

    public MiaoshaUser getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null){
            addCookie(response, token, user);
        }
        return user;
    }

    /**
     * // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
     * 告诉你为什么千万不要先删缓存，再更新数据库，在回填缓存。当一个写线程A将缓存删除，此时来
     * 了一个读线程B，发现缓存没有，去读库，然后放入缓存，那
     * @param token
     * @param id
     * @param formPass
     * @return
     */
    public boolean updatePassword(String token, long id, String formPass){
        MiaoshaUser user = getUserById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.USER_NOTEXIT_ERROR);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPass2DBPass(formPass, user.getSalt()));
        userDao.update(toBeUpdate);

        //处理缓存
        redisService.delete(MiaoShaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoShaUserKey.getById, ""+id,user);

        return true;
    }

    /**
     *  生成一个token,放入缓存并写回页面
     * @param response
     * @param user
     */
    private  void addCookie(HttpServletResponse response, String token, MiaoshaUser user){
        redisService.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoShaUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");//cookie生效的路径
        response.addCookie(cookie);
    }

   /* @Transactional
    public void inserUser(){
        User user = new User();
        user.setId(2);
        user.setName("Tom");
        userDao.insertUser(user);

        User user2 = new User();
        user.setId(3);
        user.setName("Tommy");
        userDao.insertUser(user);

    }*/
}
