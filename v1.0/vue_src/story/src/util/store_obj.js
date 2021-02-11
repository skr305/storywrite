let store_util = {}

store_util.store_local = (item_name, obj) => {
    localStorage.setItem(item_name, JSON.stringify(obj))
}

store_util.get_local = (item_name) => {
    return JSON.parse(localStorage.getItem(item_name))
}

store_util.store_session = (item_name, obj) => {
    sessionStorage.setItem(item_name, JSON.stringify(obj))
}

store_util.get_session = (item_name) => {
    return JSON.parse(sessionStorage.getItem(item_name))
}


export default store_util
