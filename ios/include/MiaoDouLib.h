//
//  MiaoDouLib.h
//  MiaoDouLib
//
//  Created by 杨利兵 on 2019/1/11.
//  Copyright © 2019 App. All rights reserved.
//

#ifndef MiaoDouLib_h
#define MiaoDouLib_h

#import <Foundation/Foundation.h>

// SDK 版本
#define SDK_VERSION  @"3.3.0"

typedef NS_ENUM(NSInteger, ErrorType){
    ERR_SUCCESS                     = 1,      // 成功
    ERR_UNKNOWN                     = 0,      // 未知错误
    ERR_OPENING                     = 10,     // 正在开门，不能重复操作
    ERR_APP_KEY_MISS                = -1000,  // APP_ID 缺失
    ERR_DEVICE_ADDRESS_EMPTY        = -2001,  // 设备 mac 地址为空
    ERR_BLUETOOTH_DISABLE           = -2002,  // 蓝牙未开启
    ERR_DEVICE_INVALID              = -2003,  // 设备失效
    ERR_DEVICE_CONNECT_FAIL         = -2004,  // 与设备建立连接失败
    ERR_DEVICE_OPEN_FAIL            = -2005,  // 开门失败
    ERR_DEVICE_DISCONNECT           = -2006,  // 与设备断开连接
    ERR_DEVICE_PARSE_RESPONSE_FAIL  = -2007,  // 解析数据失败
    ERR_APP_ID_MISMATCH             = -2008,  // APP_KEY 与应用不匹配
    ERR_NO_AVAILABLE_DEVICES        = -2009,  // 附近没有可用设备
    ERR_DEVICE_SCAN_TIMEOUT         = -2010,  // 设备扫描超时
    ERR_DEVICE_CONNECT_TIMEOUT      = -2011,  // 设备连接超时
    ERR_KEY_STRING_PARSE_FAIL       = -2012,  // 钥匙信息解析失败
    ERR_SHAKE_KEY                   = -2013,  // 摇一摇钥匙参数信息有误
    ERR_OPEN_PARAMETER_WRONG        = -2014,  // 开门参数中存在nil或空字符串
    ERR_BLE_SERVICE_FOUND_FAILURE   = -2015,  // 蓝牙服务发现失败
    ERR_BLE_CHARACTER_FOUND_FAILURE = -2016,  // 蓝牙特征值发现失败
    ERR_BLE_UPDATE_VALUE_FAILURE    = -2017,  // 获取蓝牙订阅值错误
    ERR_KEY_TIMEOUT                 = -2018,  // 钥匙有效期失效
    ERR_PHONE_INVALID               = -2019,  // 当前手机不支持BLE蓝牙
    ERR_DEVICE_CALLBACK_TIMEOUT     = -2020,  // 错误设备回调超时
    ERR_OPEN_TIMEOUT                = -2021   // 开门超时
};

/**
 * 开门参数
 * 参见 https://git.yanglb.com/md-open/api/wikis/api#获取开锁命令
 */
@interface OpenDoorModel : NSObject
-(instancetype) initWithDictionary:(NSDictionary *) data;

/**
 * 蓝牙名称
 */
@property (nonatomic, copy) NSString *ssid;

/**
 * MAC地址
 */
@property (nonatomic, copy) NSString *mac;

/**
 * 蓝牙服务ID
 */
@property (nonatomic, copy) NSString *serviceUUID;

/**
 * 蓝牙写特征ID，用于写入数据。
 */
@property (nonatomic, copy) NSString *writeUUID;

/**
 * 蓝牙通知征ID，用于读取数据。
 */
@property (nonatomic, copy) NSString *notifyUUID;

/**
 * 16进制蓝牙数据字符串
 */
@property (nonatomic, copy) NSString *key_content;

/**
 * 成功开门后设备返回的数据
 */
@property (nonatomic, copy) NSString *callback_success;
@end


@interface MiaoDouLib : NSObject

/**
 * 初始化
 */
+(void) initLib: (NSString *) appKey;

/**
 * 开门
 * @param data 开门数据
 * @param handler 开门结果回调
 */
+(void) openDoor:(OpenDoorModel*) data
      completion:(void (^)(ErrorType error)) handler;

/**
 * 获取错误消息
 */
+(NSString*) errorMsg:(ErrorType) error;
@end

#endif
