/**
 * 正则校验
 */

// 手机号正则
export const phoneRegex = /^((13[0-9])|(14[0-9])|(15([0-9]))|(16([0-9]))|(17[0-9])|(18[0-9])|(19[0-9]))\d{8}$/;

// 验证码正则
export const codeRegex = /^[0-9]{4}$/;

// 密码正则 8到20位，至少一个字母和一个数字
export const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;

// 邮箱正则
export const emailRegex = /^([A-Za-z0-9_\-.])+@([A-Za-z0-9_\-.])+\.([A-Za-z]{2,4})$/;


// 数字正则
export const numberRegex = /(^[\-0-9][0-9]*(.[0-9]+)?)$/;

/**校验只含有中文、数字、字母； */
export const validateOnlyString = /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/;