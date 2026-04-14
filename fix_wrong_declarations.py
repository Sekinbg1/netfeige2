#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复被错误修改的异常变量声明
将 "Exception fileOutputStream = null;" 改回 "FileOutputStream fileOutputStream = null;"
将 "Exception cursor = null;" 改回 "Cursor cursor = null;"  
等等
"""
import os
import re

def fix_wrong_declarations(file_path):
    """修复错误的变量类型声明"""
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
        
        original_content = content
        
        # FileHelper.java 修复
        if 'FileHelper.java' in file_path:
            # Exception fileOutputStream -> FileOutputStream fileOutputStream  
            content = re.sub(r'Exception\s+fileOutputStream\s*=\s*null;', 
                           'FileOutputStream fileOutputStream = null;', content)
            
            # Exception e = null; (在catch块外的) - 删除这些行
            content = re.sub(r'\s*Exception e = null;\s*\n\s*e = e\d+;', '', content)
            content = re.sub(r'\s*Throwable th = null;\s*\n\s*th = th\d+;', '', content)
        
        # DBHelper.java 修复
        elif 'DBHelper.java' in file_path:
            # Exception cursor -> Cursor cursor
            content = re.sub(r'Exception\s+cursor\s*=\s*null;', 
                           'Cursor cursor = null;', content)
            
            # Exception sQLiteDatabase -> SQLiteDatabase sQLiteDatabase
            content = re.sub(r'Exception\s+sQLiteDatabase\s*=\s*null;', 
                           'SQLiteDatabase sQLiteDatabase = null;', content)
            
            # Exception/Throwable z/th = null; z/th = false/value; - 删除
            content = re.sub(r'\s*Exception\s+z\s*=\s*null;\s*\n\s*z\s*=\s*false;', '', content)
            content = re.sub(r'\s*Throwable\s+th\s*=\s*null;\s*\n\s*th\s*=\s*\w+\d*;', '', content)
        
        # Public_Tools.java 修复
        elif 'Public_Tools.java' in file_path:
            # 删除错误的变量声明和赋值
            content = re.sub(r'\s*Exception\s+e\s*=\s*null;\s*\n\s*e\s*=\s*e\d+;', '', content)
            content = re.sub(r'\s*Throwable\s+th\s*=\s*null;\s*\n\s*th\s*=\s*th\d+;', '', content)
        
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed: {os.path.basename(file_path)}")
            return True
        return False
        
    except Exception as e:
        print(f"Error fixing {file_path}: {e}")
        return False

def main():
    files_to_fix = [
        r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java\com\geniusgithub\mediarender\util\FileHelper.java",
        r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java\com\netfeige\common\DBHelper.java",
        r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java\com\netfeige\common\Public_Tools.java",
    ]
    
    for file_path in files_to_fix:
        if os.path.exists(file_path):
            fix_wrong_declarations(file_path)
    
    print("Done!")

if __name__ == "__main__":
    main()
