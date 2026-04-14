#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复异常变量作用域问题
在catch块开始时声明Exception/Throwable变量
"""
import os
import re

def fix_exception_scope_in_file(file_path):
	"""修复单个文件中的异常变量作用域问题"""
	try:
		with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
			content = f.read()

		original_content = content

		# 查找并修复 catch 块中的变量赋值模式
		# 模式: } catch (...) {
		# 					e = e2;  (或 th = th2;)
		# 					e.printStackTrace();

		# 匹配catch块开始和随后的赋值
		pattern = r'(catch\s*\([^)]+\)\s*\{)\s*\n(\s*)([a-zA-Z_]\w*)\s*=\s*([a-zA-Z_]\w*\d*);'

		def replace_func(match):
			catch_start = match.group(1)
			indent = match.group(2)
			var_name = match.group(3)
			assigned_var = match.group(4)

			# 确定变量类型
			var_type = 'Throwable' if var_name == 'th' else 'Exception'

			# 添加变量声明
			return f"{catch_start}\n{indent}{var_type} {var_name} = null;\n{indent}{var_name} = {assigned_var};"

		content = re.sub(pattern, replace_func, content)

		# 另一种模式:直接在catch行后使用未声明的变量
		# 查找 throw th; 但th未声明的情况
		lines = content.split('\n')
		new_lines = []
		i = 0

		while i < len(lines):
			line = lines[i]
			new_lines.append(line)

			# 检查是否有 throw th; 且前面没有声明
			if re.match(r'\s*throw\s+th;', line):
				# 向前查找是否已经声明了th
				found_declaration = False
				for j in range(max(0, i-20), i):
					if re.search(r'\bThrowable\s+th\b', lines[j]) or re.search(r'\bth\s*=\s*null', lines[j]):
						found_declaration = True
						break

				if not found_declaration:
					# 在当前位置前插入声明
					indent = ' ' * (len(line) - len(line.lstrip()))
					new_lines.insert(-1, f"{indent}Throwable th = null;")

			i += 1

		content = '\n'.join(new_lines)

		if content != original_content:
			with open(file_path, 'w', encoding='utf-8') as f:
				f.write(content)
			return True
		return False

	except Exception as e:
		print(f"Error fixing {file_path}: {e}")
		return False

def main():
	base_dir = r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java"

	# 根据编译错误,重点处理这些文件
	problem_files = [
		"FileHelper.java",
		"DBHelper.java",
		"Public_Tools.java",
		"AlbumImageView.java",
		"GifView.java"
	]

	files_fixed = 0

	for root, dirs, files in os.walk(base_dir):
		for filename in files:
			if filename.endswith('.java') and filename in problem_files:
				file_path = os.path.join(root, filename)
				if fix_exception_scope_in_file(file_path):
					print(f"Fixed exception scope: {filename}")
					files_fixed += 1

	print(f"\nTotal files fixed: {files_fixed}")

if __name__ == "__main__":
	main()
