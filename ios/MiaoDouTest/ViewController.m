//
//  ViewController.m
//  MiaoDouTest
//
//  Created by 杨利兵 on 2019/1/11.
//  Copyright © 2019 miaodou. All rights reserved.
//

#import "ViewController.h"
#import "Api/OpenAPI.h"
#import "../include/MiaoDouLib.h"

#define KEY_AGT_NUM @"agtNum"
#define KEY_APP_KEY @"appKey"
#define KEY_PID @"pid"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *agtNum;
@property (weak, nonatomic) IBOutlet UITextField *appKey;
@property (weak, nonatomic) IBOutlet UITextField *pid;
@property (weak, nonatomic) IBOutlet UIButton *openDoor;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *progressView;

@end

@implementation ViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationDidEnterBackground:) name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [self restoreData];
    [MiaoDouLib initLib:self.appKey.text];
}

- (void) dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIApplicationDidEnterBackgroundNotification
                                                  object:nil];
}

- (void)applicationDidEnterBackground:(id) obj {
    NSLog(@"view applicationDidEnterBackground");
    [self saveData];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == self.agtNum) {
        [self.appKey becomeFirstResponder];
        return NO;
    }
    
    if (textField == self.appKey) {
        [self.pid becomeFirstResponder];
        return NO;
    }
    
    if (textField == self.pid) {
        [self.pid resignFirstResponder];
        return NO;
    }
    
    return YES;
}

- (IBAction)openDoorClick:(id)sender {
    if (![self check]) {
        return;
    }
    
    [self.view endEditing:YES];
    [self updateUI:YES];
    [OpenAPI getData:self.agtNum.text
              appKey:self.appKey.text
                 pid:self.pid.text
          completion:^(NSDictionary * _Nullable data, NSError * _Nullable error) {
              if (error) {
                  NSLog(@"%@", error);
                  [self alert:error.description];
                  [self updateUI:NO];
                  return;
              }
              
              if (![@"0" isEqualToString:data[@"code"]]) {
                  [self alert:data[@"msg"]];
                  [self.progressView stopAnimating];
                  return;
              }
              
              [self doOpenDoor:data[@"data"]];
          }];
}

-(void) doOpenDoor: (NSDictionary *) data {
    NSLog(@"open door data: \n%@", data);
    
    OpenDoorModel *model = [[OpenDoorModel alloc] initWithDictionary:data];
    [MiaoDouLib openDoor:model completion:^(ErrorType error) {
        if (error == ERR_SUCCESS) {
            [self alert:@"开门成功"];
        } else {
            [self alert:[MiaoDouLib errorMsg:error]];
        }

        [self updateUI:NO];
    }];
}

-(void) updateUI:(BOOL) opening {
    if (opening) {
        [self.progressView startAnimating];
        [self.openDoor setEnabled:NO];
    } else {
        [self.progressView stopAnimating];
        [self.openDoor setEnabled:YES];
    }
}

- (BOOL) check {
    if ([self.agtNum.text isEqualToString:@""]) {
        [self alert:@"服务端认证编号 不能为空"];
        [self.agtNum becomeFirstResponder];
        return NO;
    }
    
    if ([self.appKey.text isEqualToString:@""]) {
        [self alert:@"App Key 不能为空"];
        [self.appKey becomeFirstResponder];
        return NO;
    }
    
    if ([self.pid.text isEqualToString:@""]) {
        [self alert:@"硬件ID 不能为空"];
        [self.pid becomeFirstResponder];
        return NO;
    }
    
    return YES;
}

- (void) restoreData {
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *v;
    v = [ud stringForKey:KEY_AGT_NUM];
    if (v) {
        [self.agtNum setText:v];
    }
    
    v = [ud stringForKey:KEY_APP_KEY];
    if (v) {
        [self.appKey setText:v];
    }
    
    v = [ud stringForKey:KEY_PID];
    if (v) {
        [self.pid setText:v];
    }
}

- (void) saveData {
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    
    [ud setValue:self.agtNum.text forKey:KEY_AGT_NUM];
    [ud setValue:self.appKey.text forKey:KEY_APP_KEY];
    [ud setValue:self.pid.text forKey:KEY_PID];
}

- (void) alert: (NSString*) msg {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"妙兜SDK测试"
                                                                   message:msg
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK"
                                                 style:UIAlertActionStyleDefault
                                               handler:^(UIAlertAction * _Nonnull action) {
                                                   NSLog(@" ==== OK ==== ");
                                               }];
    [alert addAction:ok];
    [self presentViewController:alert animated:YES completion:nil];
}

@end
