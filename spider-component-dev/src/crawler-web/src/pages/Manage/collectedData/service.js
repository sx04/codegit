import Model from '../../../api';
import { BASEURL } from '../../../common/config';

/** 获取项目列表 */
// export const apiGetProjectList = (param) => {
//     return Model.get(
//         BASEURL + '/query/data/list',
//         param,
//         true,
//         true
//     )
// }

/** 获取项目列表 */
export const apiGetProjectList = ({taskId, projectName, Data,  userName}) => {
    return Model.get(
        BASEURL + '/query/data/list',
        {
            taskId,//任务id
            userName,//用户名
            Data, // 项目名称
            projectName,//项目名称
           
            
        },
        true
    )
}

//获项目名称
export const apoGetProjectName = ()=>{
    return Model.get(
        BASEURL + '/query/admin/templateCount',
        {},
        true
    )
}


/** 获取所有人员信息 */
export const apiGetAllUser = () => {
    return Model.get(
        BASEURL + '/user/query/all',
        {},
        true
    )
}


/**获取条数据详情 */
export const apiGetProDetail = ({Id, projectName}) =>{
    return Model.get(
        BASEURL+'/query/data/one',
        {   
            Id,
            projectName
        },
        true
    )
}

