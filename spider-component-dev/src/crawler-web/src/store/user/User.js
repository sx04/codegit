import { action, observable } from 'mobx';
// import { getUserInfo } from './service';
import storage from '../../common/utils/storage';

export default class User {
    // 数据
    @observable name = '' // 用户名
    @observable roles = [] // 角色

    /** 获取个人信息 */
    @action
    getUserInfo = async () => {
        // await getUserInfo().then(res => {
        //     if(res.code === 200){
        //         this.name = (res.data.phone!==null) ? res.data.phone : res.data.email
        //     }
        // });
        this.name = storage.getSession('userName');
        this.roles = storage.getSession('userRoles');
    };

    @action
    setUserName = name => {
        this.name = name
    }
    

    @action
    setUserRoles = roles => {
        this.roles = roles
    }

}
