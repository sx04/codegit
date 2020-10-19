/** 
 * 公共组件-盒子
 * 创建：2020-09-16
 **/
import './index.scss';
import React from "react";
import { Spin } from 'antd';

export default class Index extends React.Component {
    constructor() {
        super();
        this.state = { }
    }

    render() {
        return (
            <div className="common-box" style={{width: this.props.width}}>
                <Spin spinning={this.props.spinning || false}>
                    { this.props.title &&  
                           <div className="common-b-title">
                               {this.props.title}
                               { this.props.extra &&
                                   <span className="f-right">{this.props.extra}</span>
                               }
                            </div>
                    }
                    <div className="common-b-con">{this.props.render}</div>
                </Spin>
            </div>
        )
    } 
   
}