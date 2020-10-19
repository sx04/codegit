/**
 * 侧边栏菜单
 * 创建：2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';
import { inject, observer } from "mobx-react";
import { Menu } from 'antd';
import { ADMIN_SIDEMENU, USER_SIDEMENU, ADMINROLE } from '../../common/config';
import { isPermission } from '../../common/utils/common';

const { SubMenu } = Menu;

@inject("user")
@observer
class SideMenu extends Component{
    constructor(props) {
        super(props);
        this.state = {}
    }

    render(){
        const roles = this.props.user.roles;
        const menuList = isPermission(roles, ADMINROLE) ? ADMIN_SIDEMENU : USER_SIDEMENU;

        const selectedKeys = [this.getLocation()]
        return (
            <Menu
                onClick={val=>{
                    this.setState({
                        defaultSelectedKeys: [val.key]
                    })
                    if(isPermission(roles, ADMINROLE)){
                        this.props.history.push('/manage/' + val.key);
                    }else{
                        this.props.history.push('/user/' + val.key);
                    }
                }}
                mode="inline"
                theme="dark"
                defaultSelectedKeys={['home']}
                selectedKeys={selectedKeys}
            >
                {
                    menuList && menuList.map((item) => {
                        if(item.children && item.children.length){
                            return (
                                <SubMenu
                                    key={item.key}
                                    title={item.name}
                                >
                                    {
                                        item.children.map((subItem) => {
                                            return (
                                            <Menu.Item key={subItem.key}>{subItem.name}</Menu.Item>
                                            )
                                        })
                                    }
                                </SubMenu>
                            )
                        }else{
                            return (
                                <Menu.Item key={item.key}>{item.name}</Menu.Item>
                            )
                        }
                    })
                }
            </Menu>
        );
    }

    /** 获取当前页定位 */
    getLocation = () => {
        let pathname = this.props.location.pathname;
        pathname = pathname.split('/');
        pathname = pathname[pathname.length - 1];
        return pathname;
    }
}

export default SideMenu;