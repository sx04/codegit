/**
 * 页头
 * 创建：2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';
import { inject, observer } from "mobx-react";
import storage from '../../common/utils/storage';

@inject("user")
@observer
class Header extends Component{
    
    componentDidMount(){
        this.props.user.getUserInfo();
    }

    render(){
        return (
            <div className="basic-layout-header">
                <span className="basic-lh-logo f-left"></span>
                <div className="basic-lh-user f-right">
                    <span>欢迎你，{this.props.user.name}</span>
                    <span className="basic-lhu-exit" onClick={this.handleExit}>退出</span>
                </div>
            </div>
        );
    }

    /** 处理退出 */
    handleExit = () => {
        storage.clearAllSession();
        this.props.history.push('/login');
    }
}

export default Header;