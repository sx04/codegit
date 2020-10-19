package com.cetcbigdata.varanus.constant;

/**
 * @author sunjunjie
 * @date 2020/8/28 9:15
 */
public enum ErrorCode {

    LICENSE_EMPTY_ERROR(110,"License信息不存在，请联系管理员导入License！"),
    LICENSE_VERIFY_ERROR(120,"License已过期，请在后台管理系统重新导入有效license！"),
    SUCCESS(200,"请求成功"),
    BED_REQUEST(400,"请求异常"),
    UNAUTHORIZED(401,"没有进行认证或者认证非法"),
    USER_NOT_EXIST(402,"用户名或者密码错误"),
    REQUEST_BLOCK(403,"非法请求已进入黑名单"),
    USER_INVALID(455,"用户已禁用"),
    PERMISSION_DENIED(405,"所请求的Http方法不允许当前认证用户访问"),
    PARAMETER_MISSING_ERROR(429,"参数缺失"),
    PARAMETER_ERROR(430,"参数有误"),
    PARAMETER_FORMAT_ERROR(431,"参数格式有误"),
    USERNAME_PASSWORD_WORING(432,"用户名或密码错误"),
    VERIFYCODE_WORING(433,"验证码错误或已失效"),
    USER_ALREDY_EXIST(434,"用户已经存在"),
    DATABASE_OPTION_ERROR(435,"操作数据库失败"),
    SEND_SMS_FAILD(436,"发送短信失败"),
    SAVE_FAILD(437,"保存错误"),
    SELECT_TEMPLATE_ERROR(438,"查询模板信息出错"),
    TEMPLATELIST_VERTIFY_ERROR(439,"模板列表验证错误"),
    TEMPLATEDETAIL_VERTIFY_ERROR(440,"模板详情验证错误"),
    TEMPLATEALL_VERTIFY_ERROR(441,"模板验证出错"),
    FILE_SIZE_LIMIT(442,"上传文件过大"),
    HTTPMETHOD_NOT_MATCH(443,"请求的GET POST和接口不一致"),
    FILE_TYPE_LIMIT(444,"上传文件格式不支持"),
    FILE_UPLOAD_FAIL(445,"上传文件失败"),
    FILE_SIZE_ZERO(446, "文件内容空"),
    IMEI_NOT_EXIST(447,"无法识别该设备"),
    AUTHORIZED_EXPIRED(448,"token过期"),
    PROJECT_FILE_DELETE_FAIL(449,"项目字段删除失败"),
    PROJECT_FILE_CREATE_FAIL(450,"项目字段新增失败"),
    PROJECT_DELETE_ERROR(451,"项目删除失败"),
    PROJECT_CREATE_ERROR(452,"项目创建失败"),
    PROJECT_START_ERROR(453,"项目启动失败"),
    PROJECT_EDIT_ERROR(454,"项目信息修改失败"),
    PROJECTUSER_SAVE_ERROR(455,"项目用户保存失败"),
    PROJECTFILE_SAVE_ERROR(456,"项目字段保存失败"),
    USER_SAVE_ERROR(457,"用户保存失败"),
    USERTASK_FIND_ERROR(458,"用户工作量查询失败"),
    WARNING_UPDATE_ERROR(459,"报警状态修改失败"),
    FILE_GET_FAIL(460,"未上传文件"),
    TASK_TAKE_ERROR(461,"任务领取失败"),
    TASK_DELETE_ERROR(462,"任务删除失败"),
    TEMPLATE_START_FAIL(463,"模板启动失败"),
    TEMPLATE_DELETE_FAIL(464,"模板删除失败"),
    DATA_DELETE_FAIL(465,"采集数据删除失败"),
    TEMPLATE_BREAK_FAIL(466,"模板拆分失败"),
    TEMPLATE_DETAIL_FAIL(466,"模板详情存储失败"),
    TEMPLATE_IS_COMMON(466,"模板共用无法删除"),
    TABLE_SAVE_ERROR(470,"表格类信息保存失败"),
    TASK_SAVE_ERROR(471,"任务信息保存失败"),
    TABLE_COLUMN_ADD_ERROR(472,"表格新增列失败"),
    TABLE_COLUMN_DELETE_ERROR(473,"表格删除列失败"),

    DATA_NOT_EXIST(474, "数据不存在"),
    WRITE_FAVORITE_TYPE_NAME_WRONG(475,"已存在该分类"),
    WRITE_MODE_MISSING(476,"写作模板无法找到"),
    WRITE_MODE_DOC_MISSING(477,"写作模板公文实例无法找到"),
    FIELD_LENGTH_LIMIT(478,"输入字段过大长"),
    WRITE_OUTLINE_GENERATE_FAILED(479,"提纲生成公文失败"),
    GET_WECHATINFO_ERROR(480,"获取微信信息失败"),
    GROUP_OPER_ERROR(481, "获取项目查询信息失败！"),
    ENTERPRISE_SAVE_ERROR(482,"企业信息保存失败"),
    ENTERPRISE_GET_ERROR(483,"获取企业信息失败"),
    ENTERPRISE_IS_MATCHING(484,"项目匹配中，请稍后！"),
    PHONE_DECODE_ERROR(485,"小程序手机号解析失败"),
    PERSONAL_ERROR_CODE(600, "自定义错误码");

    private int code;
    private String msg;

    private ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }
}
