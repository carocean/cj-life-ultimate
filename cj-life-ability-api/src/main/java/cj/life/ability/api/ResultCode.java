package cj.life.ability.api;

public enum ResultCode {

  /* 成功状态码 */
  SUCCESS("20000", "成功"),
  /* 参数错误 */
  PARAM_IS_INVALID("1001", "参数无效"),
  PARAM_IS_BLANK("1002", "参数为空"),
  PARAM_TYPE_BIND_ERROR("1003", "参数类型错误"),
  PARAM_NOT_COMPLETE("1004", "参数缺失"),
  /* 用户错误 2001-2999*/
  USER_NOTLOGGED_IN("2001", "登录失败"),
  USER_LOGIN_ERROR("2002", "账号不存在或密码错误"),
  USER_SIGN_CHECK("2003", "验签失败"),
  /* 系统及http协议错误 4001-4999*/
  SYSTEM_ERROR("4005", "系统异常，请稍后重试"),
  NOTFOUND_ERROR("4004", "资源不存在");

  private String code;
  private String message;

  private ResultCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String code() {
    return this.code;
  }
  public String message() {
    return this.message;
  }
}
