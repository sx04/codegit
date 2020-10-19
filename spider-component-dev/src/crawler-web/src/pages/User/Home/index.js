/**
 * 普通用户-首页
 * 创建： 2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';

class UserHome extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    render() { 
        return ( 
            <div className="user-home">
                普通用户首页
            </div>
         );
    }
}
 
export default UserHome;