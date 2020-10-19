import Model from '../../../api';
import { BASEURL } from '../../../common/config';

/** 获取项目列表 */
export const apiGetProjectList = () => {
    return Model.get(
        BASEURL + '/project/query',
        {},
        true
    )
}


/** 启动/关闭项目 */
export const apiChangeProStatus = ({code, isRun}) => {
    return Model.post(
        BASEURL + '/project/start',
        {
            code, 
            isRun
        },
        true
    )
}