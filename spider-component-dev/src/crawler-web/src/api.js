import axios from 'axios'
import { message } from 'antd'
import qs from 'qs'
import storage from './common/utils/storage';
import { isIE } from './common/utils/common';

const Axios = axios.create({
    baseURL: '/',
    timeout: 30000
})
Axios.interceptors.request.use(
    config => {
        // loading 动画
        // token 重定向
        // 后端需求是否序列化
        return config
    },
    error => {
        // loading 关闭
        // 请求超时
        if (error.code === 'ECONNABORTED' && error.message.indexOf('timeout') !== -1) {
            //console.log('timeout')
            // return service.request(originalRequest);//例如再重复请求一次
        }
        // 需要重定向到错误页面
        const errorInfo = error.response;
        if (errorInfo) {
            // error =errorInfo.data//页面那边catch的时候就能拿到详细的错误信息,看最下边的Promise.reject
            // const errorStatus = errorInfo.status; // 404 403 500 ... 等
            //console.log(errorStatus)
        }
        return Promise.reject(error)
    }
)

Axios.interceptors.response.use(response => {
    let data;
    if (response.data === undefined) {
        data = response.request.responseText
    } else {
        data = response.data
    }
    // 根据返回的code值来做不同的处理（和后端约定）
    switch (data.code) {
        case 200:
            break;  
        
        default:
           // console.log(data.msg);
    }
    // 若不是正确的返回code，且已经登录，就抛出错误
    // const err = new Error(data.description)

    // err.data = data
    // err.response = response

    // throw err
    return data
}, error => {
    if (error && error.response) {
        switch (error.response.status) {
            case 400:
                error.message = '请求错误'
                break
            case 403:
                error.message = '拒绝访问'
                break
            case 401:
                error.message = '登录过期，请重新登录。'
                window.location.href = '/#/login'
                break;  
            case 404:
                error.message = `请求地址出错: ${error.response.config.url}`
                break
            case 408:
                error.message = '请求超时'
                break
            case 500:
                // error.message = '服务器内部错误'
                //解决用户token认证过期问题,采用http响应500时，根据返回的message进行判断
                console.log(error.response.data.message)
                error.message = '登录过期，请重新登录。'
                if(error.response.data.message&&error.response.data.message === 'Token timeout') {
                    window.location.href = '/#/login'
                }
                break
            default:
        }
       
        message.destroy();
        message.error('发生错误： '+error.message);
        return Promise.reject(error)
    }
})
export default {
    post(url, data, isToken, isJson) {
        const headers = {
          'X-Requested-With': 'XMLHttpRequest',
          'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        };
        if (isToken) {
          headers.Authorization = storage.getSession('token');
        }
        if (isJson) {
          headers['Content-Type'] = 'application/json; charset=UTF-8';
        }
        return Axios({
          method: 'post',
          url,
          data: isJson ? data : qs.stringify(data),
          headers,
        });
      },
      get(url, params, isToken, isJson) {
        const headers = {
          'X-Requested-With': 'XMLHttpRequest',
          'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        };
        if (isToken) {
          headers.Authorization = storage.getSession('token');
        }
        if (isJson) {
          headers['Content-Type'] = 'application/json; charset=UTF-8';
        }
        if(isIE()){
            headers['Cache-Control'] = 'no-cache';
            headers['Pragma'] = 'no-cache';
        }
        return Axios({
          method: 'get',
          url,
          params,
          headers,
        });
      },
}
