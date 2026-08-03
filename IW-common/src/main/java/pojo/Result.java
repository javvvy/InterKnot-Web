package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;
    public static Result success(Object data) {
        Result result = new Result();
        result.code = "1";
        result.msg = "success";
        result.data = data;
        return result;
    }
    public static Result success(){
        Result result = new Result();
        result.code = "1";
        result.msg = "success";
        return result;
    }
    public static Result successMsg(String msg){
        Result result = new Result();
        result.code = "1";
        result.msg = msg;
        return result;
    }
    public static Result error() {
        Result result = new Result();
        result.code = "0";
        result.msg = "error";
        return result;
    }
    public static Result fail(String msg) {
        Result result = new Result();
        result.code = "0";
        result.msg = msg;
        return result;
    }
}
