import Model from '../../../api';
import { BASEURL } from '../../../common/config';

// export const apiGetcrawAlarm = () =>{
//     return Model.get(
//         BASEURL + '/query/taskWarning/list',
//         {

//         },
//         true
//     )
// }

export const apiGetcrawAlarm = (param) =>{
    return Model.get(
        BASEURL + '/query/taskWarning/list',
        param,
        true,
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