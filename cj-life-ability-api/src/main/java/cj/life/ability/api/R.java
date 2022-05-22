package cj.life.ability.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {
    private String code;
    private String message;
    private T data;

    public T getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static <T> R<T> of( String code, String msg,T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> of(ResultCode rc,T data) {
        return new R<>(rc.code(), rc.message(), data);
    }
}
