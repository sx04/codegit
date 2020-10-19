/**
 * 公共组件-无数据
 * 创建： 2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';

class Index extends Component {
     constructor(props) {
         super(props);
         this.state = {  }
     }
     render() { 
         return ( 
             <div className="common-nodata">暂无相关数据</div>
          );
     }
}
  
 export default Index;