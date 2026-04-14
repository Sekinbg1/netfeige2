#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清理所有友盟相关的引用和调用
"""
import os
import re

def clean_umeng_references(file_path):
    """清理文件中的友盟引用"""
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
        
        modified = False
        new_lines = []
        
        for line in lines:
            # 跳过友盟导入(应该已经被删除了)
            if 'import com.umeng.' in line or 'import u.aly.' in line:
                continue
            
            # 注释掉友盟调用
            if any(umeng_call in line for umeng_call in [
                'MobclickAgent.',
                'AnalyticsConfig.',
                'ReportPolicy.',
                'Gender.',
                'UMPlatformData'
            ]):
                new_lines.append('// Umeng removed: ' + line)
                modified = True
            else:
                new_lines.append(line)
        
        if modified:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.writelines(new_lines)
            return True
        return False
        
    except Exception as e:
        print(f"Error cleaning {file_path}: {e}")
        return False

def main():
    base_dir = r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java"
    
    files_cleaned = 0
    
    for root, dirs, files in os.walk(base_dir):
        for filename in files:
            if filename.endswith('.java'):
                file_path = os.path.join(root, filename)
                if clean_umeng_references(file_path):
                    print(f"Cleaned: {filename}")
                    files_cleaned += 1
    
    print(f"\nTotal files cleaned: {files_cleaned}")

if __name__ == "__main__":
    main()
