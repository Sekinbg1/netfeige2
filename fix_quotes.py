#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复Java源文件中的编码问题 - 添加缺失的闭合双引号
"""

import os
import re

# 定义需要修复的文件和对应的字符串映射(添加闭合双引号)
fixes = {
	'app/src/main/java/com/netfeige/common/Public_Tools.java': [
		('收到来自讨论组', '收到来自讨论组"'),
		('的消息"', '的消息";'),  # 第393行
		(': "', ': "'),  # 第398行 - 保持原样
		('给您发来飞信消息,"', '给您发来飞信消息,";'),  # 第399行
		('程序退出', '程序退出"'),
		('上一级"', '上一级");'),  # 第778,779行
	],
	'app/src/main/java/com/netfeige/display/data/DataSource.java': [
		('接受了您的打印请求。', '接受了您的打印请求。";'),
		('为您打印已完成。', '为您打印已完成。";'),
		('拒绝了您的打印请求。"', '拒绝了您的打印请求。";'),
		('长时间未响应，您可以再次请求或选择其他打印机。', '长时间未响应，您可以再次请求或选择其他打印机。";'),
		('对方拒绝了您的打印请求。', '对方拒绝了您的打印请求。";'),
	],
	'app/src/main/java/com/netfeige/display/data/FileAdapter.java': [
		('上一级"', '上一级");'),
	],
	'app/src/main/java/com/netfeige/display/data/IpmsgApplication.java': [
		('发送失败', '发送失败"'),
	],
	'app/src/main/java/com/netfeige/display/ui/ChoiceUserActivity.java': [
		('无法获取要发送的文件路径，发送失败', '无法获取要发送的文件路径，发送失败"'),
		('文件已发送', '文件已发送"'),
	],
	'app/src/main/java/com/netfeige/display/ui/dialog/CheckLanSharePwdDialog.java': [
		('的访问密码', '的访问密码"'),
	],
	'app/src/main/java/com/netfeige/display/ui/FileActivity.java': [
		('下一步', '下一步"'),
	],
	'app/src/main/java/com/netfeige/display/ui/NewDiscussActivity.java': [
		('讨论组名称不能为空', '讨论组名称不能为空"'),
	],
	'app/src/main/java/com/netfeige/display/ui/OptionActivity.java': [
		('SD卡"', 'SD卡");'),
		('关"', '关");'),
	],
	'app/src/main/java/com/netfeige/display/ui/SetFeigeDownloadActivity.java': [
		(',飞鸽收件夹', ',飞鸽收件夹"'),
	],
	'app/src/main/java/com/netfeige/display/ui/wifi/WifiMainActivity.java': [
		(' </font>人连接', ' </font>人连接"'),
	],
	'app/src/main/java/com/netfeige/filemanager/FileManager.java': [
		('目标文件夹是源文件夹的子文件夹，不能粘贴，', '目标文件夹是源文件夹的子文件夹，不能粘贴，";'),
	],
	'app/src/main/java/com/netfeige/protocol/Protocol.java': [
		('Update信息发送', 'Update信息发送"'),
	],
	'app/src/main/java/com/netfeige/service/IpmsgService.java': [
		('暂无打印机可用', '暂无打印机可用"'),
		('您被移出了讨论组，', '您被移出了讨论组，"'),
		('拒绝了讨论组邀请', '拒绝了讨论组邀请"'),
	],
}

def fix_file(file_path, replacements):
	"""修复单个文件"""
	if not os.path.exists(file_path):
		print(f"文件不存在: {file_path}")
		return False

	try:
		with open(file_path, 'r', encoding='utf-8') as f:
			text = f.read()

		original_text = text
		for old_str, new_str in replacements:
			if old_str in text:
				text = text.replace(old_str, new_str)
				print(f"  替换: '{old_str}' -> '{new_str}'")

		if text != original_text:
			# 写回文件
			with open(file_path, 'w', encoding='utf-8') as f:
				f.write(text)
			print(f"✓ 已修复: {file_path}")
			return True
		else:
			print(f"- 无需修复: {file_path}")
			return True

	except Exception as e:
		print(f"✗ 处理文件失败 {file_path}: {e}")
		import traceback
		traceback.print_exc()
		return False

def main():
	base_dir = os.path.dirname(os.path.abspath(__file__))
	os.chdir(base_dir)

	print("开始修复Java文件缺失的闭合双引号...\n")

	success_count = 0
	fail_count = 0

	for file_path, replacements in fixes.items():
		if fix_file(file_path, replacements):
			success_count += 1
		else:
			fail_count += 1
		print()

	print(f"\n修复完成!")
	print(f"成功: {success_count} 个文件")
	print(f"失败: {fail_count} 个文件")

if __name__ == '__main__':
	main()
