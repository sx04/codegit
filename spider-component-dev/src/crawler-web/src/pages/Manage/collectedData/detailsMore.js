import "./index.scss";
import React, { Component } from "react";
import { apiGetProDetail } from "./service";
import { CloseOutlined } from '@ant-design/icons';



class detailsMore extends Component{

    constructor(props){
        super(props);
        this.state={
            prodata:[],

        };
    }
    render(){
        return(
            <div className="temp-add-pop pop-wrap">
            <div className="pop-content">
              <span className="pop-close" onClick={this.props.handleClose}>
                <CloseOutlined />
              </span>
              <h2 className="pop-title">文章详情</h2>
              {this.renderPro()}
            </div>
          </div>
        )
    }

    renderPro=()=>{
        
    }

}

export default detailsMore;