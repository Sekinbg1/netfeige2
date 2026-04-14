#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清空友盟SDK相关文件内容,替换为空的Stub类以避免编译错误
"""
import os
import re

def create_stub_class(file_path, package, class_name):
	"""创建空的Stub类"""
	stub_content = f"""package {package};

// Stub class to avoid compilation errors
public class {class_name} {{
    // Empty stub implementation
}}
"""

	with open(file_path, 'w', encoding='utf-8') as f:
		f.write(stub_content)
	print(f"Created stub for: {class_name}")

def process_umeng_files():
	"""处理所有友盟相关文件"""
	base_dir = r"F:\Documents\AndroidStudioProjects\netfeige2\app\src\main\java"

	# 处理u.aly包
	aly_dir = os.path.join(base_dir, "u", "aly")
	if os.path.exists(aly_dir):
		for filename in os.listdir(aly_dir):
			if filename.endswith('.java'):
				file_path = os.path.join(aly_dir, filename)
				class_name = filename[:-5]  # 去掉.java后缀

				# 读取文件获取类名和包名
				try:
					with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
						content = f.read()

					# 提取包名
					package_match = re.search(r'package\s+([\w.]+);', content)
					package = package_match.group(1) if package_match else "u.aly"

					# 检查是否有编译错误的特征
					if ('android.support.v4' in content or
							'FrameMetricsAggregator' in content or
							'EnvironmentCompat' in content or
							'co...' in content):  # 可变参数语法错误
						create_stub_class(file_path, package, class_name)
						print(f"Replaced problematic file: {filename}")

				except Exception as e:
					print(f"Error processing {filename}: {e}")

	# 处理com.umeng包
	umeng_dir = os.path.join(base_dir, "com", "umeng")
	if os.path.exists(umeng_dir):
		for root, dirs, files in os.walk(umeng_dir):
			for filename in files:
				if filename.endswith('.java'):
					file_path = os.path.join(root, filename)
					try:
						with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
							content = f.read()

						# 如果引用了u.aly包且有编译问题,创建stub
						if 'import u.aly.' in content:
							# 提取包名和类名
							package_match = re.search(r'package\s+([\w.]+);', content)
							class_match = re.search(r'(?:public|private|protected)?\s*(?:abstract)?\s*class\s+(\w+)', content)

							if package_match and class_match:
								package = package_match.group(1)
								class_name = class_match.group(1)

								# 检查是否引用了有问题的u.aly类
								if any(cls in content for cls in ['u.aly.bq', 'u.aly.br', 'u.aly.dn', 'u.aly.cd']):
									create_stub_class(file_path, package, class_name)
									print(f"Replaced umeng file: {filename}")

					except Exception as e:
						print(f"Error processing {filename}: {e}")

if __name__ == "__main__":
	print("Processing Umeng SDK files...")
	process_umeng_files()
	print("Done!")
