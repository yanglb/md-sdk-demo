//
//  OpenAPI.m
//  MiaoDouTest
//
//  Created by 杨利兵 on 2019/1/11.
//  Copyright © 2019 miaodou. All rights reserved.
//

#import "OpenAPI.h"

#define TIMEOUT 5
#define BASE_URL @"http://121.40.204.191:18080/mdserver/"

@implementation OpenAPI

+(void) getData:(NSString *) agtNum
         appKey:(NSString*) appKey
            pid:(NSString*) pid
     completion:(void (^)(NSDictionary* _Nullable data, NSError* _Nullable error)) handler {
    
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", BASE_URL, @"service/getData"]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    
    NSString *data = [NSString stringWithFormat:@"agt_num=%@&app_key=%@&pid=%@", agtNum, appKey, pid];
    request.HTTPBody = [data dataUsingEncoding:NSUTF8StringEncoding];
    request.timeoutInterval = 5;
    
    [NSURLConnection sendAsynchronousRequest:request
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse * _Nullable response, NSData * _Nullable data, NSError * _Nullable connectionError) {
                               
                               NSLog(@" ==== 接口返回 ==== ");
                               if (connectionError) {
                                   NSLog(@"错误: %@", connectionError);
                                   handler(nil, connectionError);
                               } else {
                                   NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                                   
                                   NSError *error = nil;
                                   NSDictionary *r = [self makeResult:data error:&error];
                                   if (error) {
                                       handler(nil, error);
                                   } else {
                                       handler(r, nil);
                                   }
                               }
                           }];
}


+(NSDictionary*) makeResult: (NSData*) data error:(NSError **)error {
    NSDictionary *res = [NSJSONSerialization JSONObjectWithData:data
                                                        options:NSJSONReadingMutableContainers
                                                          error:error];
    if (*error) return nil;
    return res;
}
@end
