package cj.life.ability.api;

 class R {


    private String code;

    private String message;
    private Object data;

    private R() {

    }

    public R(ResultCode resultCode, Object data) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.data = data;
    }

    private void setResultCode(ResultCode resultCode) {
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    // 返回成功
    public static R success() {
        R result = new R();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    // 返回成功
    public static R success(Object data) {
        R result = new R();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    // 返回失败
    public static R fail(String code, String message) {
        R result = new R();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 返回失败
    public static R fail(String code, String message, String exception) {
        R result = new R();
        result.setCode(code);
        result.setMessage(message);
        result.setData(exception);
        return result;
    }

    // 返回失败
    public static R fail(ResultCode resultCode) {
        R result = new R();
        result.setResultCode(resultCode);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
