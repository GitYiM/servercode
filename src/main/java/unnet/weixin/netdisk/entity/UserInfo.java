package unnet.weixin.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author cy
 * @since 2020-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
//chain的中文含义是链式的，设置为true，则setter方法返回当前对象
@Accessors(chain = true)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 成员UserID
     */
    private String userid;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 成员所属部门id列表
     */
    private String department;

    /**
     * 职务信息
     */
    private String position;

    /**
     * 性别。0表示未定义，1表示男性，2表示女性
     */
    private String gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 地址
     */
    private String address;

    /**
     * minio的桶
     */
    private String bucket;

    /**
     * 用户名
     */
    private String ak;

    /**
     * 密码
     */
    private String sk;

    /**
     * 二维码
     */
    private String qrCode;

//    /**
//     * 已用空间
//     */
//    private Long userUsage;


}
