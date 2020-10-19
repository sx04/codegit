/** 
 * 柱状图统计
 * 2020-08-31
 **/
import React from "react";
import echarts from 'echarts';
import _isEqual from "lodash/isEqual";
import NoData from '../../NoData';

export default class Index extends React.Component {
    constructor() {
        super();
        this.state = { }
    }

    componentDidMount(){
        this.init();
    }

    componentDidUpdate(preProps) {
        if (!_isEqual(this.props, preProps)) {
            this.init();
        }
    }

    render() {
        const { barData, id, height } = this.props;
        if(!(barData && barData.length)) return <NoData />

        return (
            <div id={id} style={{width: '100%', height: height || '430px'}} key={height}></div>
        )
    } 

    init = () => {
        const { barData, id,  rotate, colors, formatter } = this.props;
        if(!(barData && barData.length && document.getElementById(id))) return;

        var dataAxis = barData && barData.map((item)=>{
            return item.label
        });
        var data = barData && barData.map((item) => {
            return {
                value: item.value,
                label: item.label,
                code: item.code
            }
        });

        let barChart = echarts.init(document.getElementById(id));

        let option = {
            grid: {
                left: 10,
                right: 30,
                top: 40,
                bottom: 10,
                containLabel: true
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {  
                    type: 'shadow'
                },
                formatter
            },
            xAxis: {
                data: dataAxis,
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#5076a5'
                    },
                    rotate
                }
            },
            yAxis: { 
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                },
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#5076a5'
                    }
                }
            },
            series: [
                {
                    type: 'bar',
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: colors[0]},
                                {offset: 1, color: colors[1]}
                            ]
                        ),
                        barBorderRadius: [6, 6, 0, 0]
                    },
                    barMaxWidth: 36,
                    data: data
                }
            ]
        };
        barChart.clear();
        barChart.setOption(option);
    }
   
}