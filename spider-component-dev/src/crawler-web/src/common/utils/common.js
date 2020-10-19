
/** 判断存在并且不能为空 */
export const isNull = val => {
  if (val) {
    return val.toString().match(/^\s*$/);
  } else {
    return true;
  }
};

/** base64转码 */
export const transcoding = string => {
  return btoa(string);
};

/** 判断是否包含某个权限 */
export const isPermission = (roles, auth) => {
  if(roles && auth){
    return roles.indexOf(auth)>-1
  }else{
    return false;
  }
}

export const isIE = () => {
  // let userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
  // let isOpera = userAgent.indexOf("Opera") > -1;
  // if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
  //   return true;
  // }
  // return false;
  if(!!window.ActiveXObject || "ActiveXObject" in window)
    return true;
  else
    return false;
}

/** 获取地址栏参数值 */
export const _param = (name = '') => {
  let url = document.location.toString();
  let arrObj = url.split("?");

  if (arrObj.length > 1) {
      let arrPara = arrObj[1].split("&");
      let arr;

      for (let i = 0; i < arrPara.length; i++) {
          arr = arrPara[i].split("=");

          if (arr != null && arr[0] === name) {
              return arr[1];
          }
      }
      return "";
  } else {
      return "";
  }
}


  
  
  