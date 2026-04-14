#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量修复编译错误
"""
import os
import re

def remove_umeng_imports(file_path):
	"""移除友盟相关的导入和使用"""
	try:
		with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
			content = f.read()

		original_content = content

		# 移除友盟导入
		content = re.sub(r'import\s+com\.umeng\..*?;\n', '', content)
		content = re.sub(r'import\s+u\.aly\..*?;\n', '', content)

		# 注释掉友盟调用
		content = re.sub(r'MobclickAgent\.\w+\([^)]*\);', '// Umeng removed: \\g<0>', content)

		if content != original_content:
			with open(file_path, 'w', encoding='utf-8') as f:
				f.write(content)
			return True
		return False
	except Exception as e:
		print(f"Error processing {file_path}: {e}")
		return False

def fix_exception_variables(file_path):
	"""修复异常变量作用域问题 - 在catch块前声明变量"""
	try:
		with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
			lines = f.readlines()

		modified = False
		new_lines = []
		i = 0

		while i < len(lines):
			line = lines[i]

			# 查找 catch 块中的赋值模式: } catch (...) { e = e2;
			if 'catch' in line and i + 1 < len(lines):
				new_lines.append(line)
				i += 1

				# 检查下一行是否有 e = e2 或 th = th2 模式
				if i < len(lines):
					next_line = lines[i]
					if re.match(r'\s*(e|th)\s*=\s*\w+\d*;', next_line):
						# 提取变量名和类型
						var_match = re.match(r'\s*(\w+)\s*=\s*(\w+\d*);', next_line)
						if var_match:
							var_name = var_match.group(1)
							assigned_var = var_match.group(2)

							# 推断类型
							var_type = 'Exception' if var_name == 'e' else 'Throwable'

							# 在catch块开始处添加变量声明
							indent = ' ' * (len(next_line) - len(next_line.lstrip()))
							declaration = f"{indent}{var_type} {var_name} = null;\n"
							new_lines.append(declaration)
							modified = True

					new_lines.append(next_line)
					i += 1
			else:
				new_lines.append(line)
				i += 1

		if modified:
			with open(file_path, 'w', encoding='utf-8') as f:
				f.writelines(new_lines)
			return True
		return False

	except Exception as e:
		print(f"Error fixing exceptions in {file_path}: {e}")
		return False

def fix_activity_chooser_view(file_path):
	"""修复ActivityChooserView引用"""
	try:
		with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
			content = f.read()

		original_content = content

		# 替换ActivityChooserView常量
		content = re.sub(
			r'ActivityChooserView\.ActivityChooserViewAdapter\.MAX_ACTIVITY_COUNT_UNLIMITED',
			'Integer.MAX_VALUE',
			content
		)

		# 添加必要的导入(如果需要)
		if 'Integer.MAX_VALUE' in content and 'import java.lang.Integer;' not in content:
			# Integer不需要导入,是java.lang包的一部分
			pass

		if content != original_content:
			with open(file_path, 'w', encoding='utf-8') as f:
				f.write(content)
			return True
		return False
	except Exception as e:
		print(f"Error fixing ActivityChooserView in {file_path}: {e}")
		return False

def main():
	base_dir = r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java"

	files_fixed = 0

	# 遍历所有Java文件
	for root, dirs, files in os.walk(base_dir):
		for filename in files:
			if filename.endswith('.java'):
				file_path = os.path.join(root, filename)

				# 修复友盟引用
				if remove_umeng_imports(file_path):
					print(f"Fixed Umeng imports: {filename}")
					files_fixed += 1

				# 修复ActivityChooserView
				if fix_activity_chooser_view(file_path):
					print(f"Fixed ActivityChooserView: {filename}")
					files_fixed += 1

	print(f"\nTotal files fixed: {files_fixed}")

if __name__ == "__main__":
	main()
