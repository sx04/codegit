/** 
 * 公共组件-信息盒子
 * 创建：2020-09-16
 **/
import './index.scss';
import React from "react";
import { Spin } from 'antd';

export default class InfoBox extends React.Component {
    constructor() {
        super();
        this.state = { }
    }

    render() {
        return (
            <div className="common-info-box" style={{width: this.props.width}}>
                <Spin spinning={this.props.spinning || false}>
                    { this.props.title &&  
                           <div className="common-ib-title">
                               <span className="common-ibt-text">{this.props.title}</span>
                               { this.props.extra &&
                                   <span className="f-right">{this.props.extra}</span>
                               }
                            </div>
                    }
                    <div className="common-ib-con">{this.props.render}</div>
                </Spin>
            </div>
        )
    } 
   
}