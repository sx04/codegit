import Model from '../../api';
import { BASEURL } from '../../common/config';

export const getUserInfo = () => {
    return Model.get(
        BASEURL + '/project/query',
        {},
        true
    )
}