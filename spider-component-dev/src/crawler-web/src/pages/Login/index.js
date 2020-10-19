/**
 * 登录页
 * 创建： 2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';
import { Form, Input, Button } from 'antd';
import { inject, observer } from "mobx-react";
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import storage from '../../common/utils/storage';

@inject("user")
@observer
class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    render() { 
        return ( 
            <div className="login-wrap">
                <div className="login-content">
                    <p className="login-c-title">欢迎来到数据采集中心</p>
                    <Form
                        className="login-c-from"
                        name="login"
                        layout="vertical"
                        onFinish={this.handleLogin}
                        onFinishFailed={this.onFinishFailed}
                        >
                        <Form.Item
                            name="username"
                            rules={[{ required: true, message: '请输入用户名' }]}
                        >
                            <Input  prefix={<UserOutlined className="site-form-item-icon" />} placeholder="用户名"/>
                        </Form.Item>

                        <Form.Item
                            name="password"
                            rules={[{ required: true, message: '请输入密码' }]}
                        >
                            <Input.Password  prefix={<LockOutlined className="site-form-item-icon" />} placeholder="密码"/>
                        </Form.Item>
                        <Form.Item>
                            <p className="login-c-info">管理员账号: admin，其他均为普通账号（测试用后删）</p>
                            <Button className="login-c-btn" htmlType="submit">登录</Button>
                        </Form.Item>
                    </Form>
                </div>
            </div>
         );
    }

    /** 处理登录 */
    handleLogin = values => {
        this.props.user.setUserName(values.username);
        storage.setSession('userName', values.username);

        if(values.username === 'admin'){ // 管理员测试号，后期登录接口写好替换@@@
            this.props.user.setUserRoles('admin,user');
            storage.setSession('userRoles', 'admin,user');
            this.props.history.push('/manage/home');
        }else{
            this.props.user.setUserRoles('user');
            storage.setSession('userRoles', 'user');
            this.props.history.push('/user/home');
        }
    }
    
    /** 登录校验失败 */
    onFinishFailed = errorInfo => {
        console.log('Failed:', errorInfo);
    };
}

export default Login;