import { combineReducers } from "redux";
import { configureStore } from "@reduxjs/toolkit";
import { persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";

import user from "./userSlice";

const reducers = combineReducers({
    user: user.reducer,
});

const persistConfig = {
  key: "root",
  storage,
  whitelist: ["userSlice"],
};


const persistedReducer = persistReducer(persistConfig, reducers);


const store = configureStore({
  reducer: persistedReducer,
})

 export default store;