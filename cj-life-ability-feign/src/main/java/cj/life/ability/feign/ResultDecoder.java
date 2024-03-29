package cj.life.ability.feign;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import cj.life.ability.api.exception.ApiException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author andrew jofers
 * @since 2022/4/8
 */
public class ResultDecoder implements Decoder {
    private Decoder decoder;

    public ResultDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Method method = response.request().requestTemplate().methodMetadata().method();
        boolean isResult = method.getReturnType() != R.class && method.isAnnotationPresent(RequestMapping.class);
        if (isResult) {
            ParameterizedTypeImpl resultType = ParameterizedTypeImpl.make(R.class, new Type[]{type}, null);
            R<?> result = (R<?>) this.decoder.decode(response, resultType);
            if (!ResultCode.SUCCESS.code().equals(result.getCode())) {
                throw new ApiException(result.getCode(), result.getMessage());
            }
            return result.getData();
        }
        return this.decoder.decode(response, type);
    }
}

