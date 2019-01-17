//
//  OpenAPI.h
//  MiaoDouTest
//
//  Created by 杨利兵 on 2019/1/11.
//  Copyright © 2019 miaodou. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OpenAPI : NSObject

+(void) getData:(NSString *) agtNum
         appKey:(NSString*) appKey
            pid:(NSString*) pid
     completion:(void (^)(NSDictionary* _Nullable data, NSError* _Nullable error)) handler;

@end

NS_ASSUME_NONNULL_END
