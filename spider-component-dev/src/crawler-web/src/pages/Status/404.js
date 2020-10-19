/**
 * 找不到页面
 * 创建： 2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';

class Index extends Component {
    constructor(props) {
        super(props);
        this.state = { 
         }
    }
    render() { 
        return ( 
            <div className="not-found">
                <p>404, 找不到页面</p>
            </div>
        );
    }
}
 
export default Index;