/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.lite;

/**
 * The Class BaseResponse.
 * 该类用来包装spring mvc http 请求,当方法上标注@ResponseBody后，返回值会被setData 部分,null string也会破封装，除非方法上有wrap(false)
 *
 * @author Xia Zhengsheng
 * @param <T> the generic type
 */
public abstract class BaseResponse<T> extends SimpleResponse {

    /***0 成功 ， -1 业务失败  ，-2 权限验证*/
    private Integer code;

    private String exeptionClass;

    private T data = null;

    /***以后业务全部返回字符串*/
    private IResponseStatus status;
    
    protected BaseResponse() {
        super();
    }

    public void setStatus(IResponseStatus status) {
        this.status = status;
        if (status != null) {
            this.setSuccess(status.getSuccess());
            this.setCode(status.getStatus());
            this.setMessage(this.status.getMessage());
        }
    }
    
    /***protected fastjson不序列化*/
    protected String _getExeptionClass() {
        return exeptionClass;
    }
    
    /***protected fastjson不序列化*/
    protected Integer _getCode() {
        return code;
    }
    
    /***不让fastjson序列化*/
    public T _getData() {
        return data;
    }
    /***不让fastjson序列化*/
    public IResponseStatus _getStatus() {
        return status;
    }

    public void setExeptionClass(String exeptionClass) {
        this.exeptionClass = exeptionClass;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
