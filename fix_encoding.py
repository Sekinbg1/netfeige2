#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复Java源文件中的编码问题
将损坏的中文字符串替换为正确的中文
"""

import os
import re

# 定义需要修复的文件和对应的字符串映射
fixes = {
	'app/src/main/java/com/netfeige/common/Public_Tools.java': [
		('鏀跺埌鏉ヨ嚜璁ㄨ缁?', '收到来自讨论组'),
		('鐨勬秷鎭?', '的消息'),
		('锛?', ': '),
		('缁欐偍鍙戞潵椋為附娑堟伅锛?', '给您发来飞信消息,'),
		('绋嬪簭閫€鍑?', '程序退出'),
		('涓婁竴绾?', '上一级'),
	],
	'app/src/main/java/com/netfeige/display/data/DataSource.java': [
		('鎺ュ彈浜嗘偍鐨勬墦鍗拌姹傘€?', '接受了您的打印请求。'),
		('涓烘偍鎵撳嵃宸插畬鎴愩€?', '为您打印已完成。'),
		('鎷掔粷浜嗘偍鐨勬墦鍗拌姹傘€?', '拒绝了您的打印请求。'),
		('闀挎椂闂存湭鍝嶅簲锛屾偍鍙互鍐嶆璇锋眰鎴栭€夋嫨鍏朵粬鎵撳嵃鏈恒€?', '长时间未响应，您可以再次请求或选择其他打印机。'),
			('瀵规柟鎷掔粷浜嗘偍鐨勬墦鍗拌姹傘€?', '对方拒绝了您的打印请求。'),
	],
	'app/src/main/java/com/netfeige/display/data/FileAdapter.java': [
		('涓婁竴绾?', '上一级'),
	],
	'app/src/main/java/com/netfeige/display/data/IpmsgApplication.java': [
		('鍙戦€佸け璐?', '发送失败'),
	],
	'app/src/main/java/com/netfeige/display/ui/ChoiceUserActivity.java': [
		('鏃犳硶鑾峰彇瑕佸彂閫佺殑鏂囦欢璺緞锛屽彂閫佸け璐?', '无法获取要发送的文件路径，发送失败'),
		('鏂囦欢宸插彂閫?', '文件已发送'),
	],
	'app/src/main/java/com/netfeige/display/ui/dialog/CheckLanSharePwdDialog.java': [
		('鐨勮闂瘑鐮?', '的访问密码'),
	],
	'app/src/main/java/com/netfeige/display/ui/FileActivity.java': [
		('涓嬩竴姝?', '下一步'),
	],
	'app/src/main/java/com/netfeige/display/ui/NewDiscussActivity.java': [
		('璁ㄨ缁勫悕绉颁笉鑳戒负绌?', '讨论组名称不能为空'),
	],
	'app/src/main/java/com/netfeige/display/ui/OptionActivity.java': [
		('SD鍗?', 'SD卡'),
		('鍏?', '关'),
	],
	'app/src/main/java/com/netfeige/display/ui/SetFeigeDownloadActivity.java': [
		(',椋為附鏀朵欢澶?', ',飞鸽收件夹'),
		('鍙敤', '可用'),
	],
	'app/src/main/java/com/netfeige/display/ui/wifi/WifiMainActivity.java': [
		(' </font>浜鸿繛鎺?', ' </font>人连接'),
	],
	'app/src/main/java/com/netfeige/filemanager/FileManager.java': [
		('鐩爣鏂囦欢澶规槸婧愭枃浠跺す鐨勫瓙鏂囦欢澶癸紝涓嶈兘绮樿创锛?', '目标文件夹是源文件夹的子文件夹，不能粘贴，'),
	],
	'app/src/main/java/com/netfeige/protocol/Protocol.java': [
		('Update淇℃伅鍙戦€?', 'Update信息发送'),
	],
	'app/src/main/java/com/netfeige/service/IpmsgService.java': [
		('鏆傛棤鎵撳嵃鏈哄彲鐢?', '暂无打印机可用'),
		('鎮ㄨ绉诲嚭浜嗚璁虹粍锛?', '您被移出了讨论组，'),
		('鎷掔粷浜嗚璁虹粍閭€璇?', '拒绝了讨论组邀请'),
	],
}

def fix_file(file_path, replacements):
	"""修复单个文件"""
	if not os.path.exists(file_path):
		print(f"文件不存在: {file_path}")
		return False

	try:
		with open(file_path, 'rb') as f:
			content = f.read()

		# 尝试UTF-8解码
		try:
			text = content.decode('utf-8')
		except UnicodeDecodeError:
			print(f"文件不是UTF-8编码: {file_path}")
			return False

		original_text = text
		for old_str, new_str in replacements:
			if old_str in text:
				text = text.replace(old_str, new_str)
				print(f"  替换: '{old_str}' -> '{new_str}'")

		if text != original_text:
			# 写回文件
			with open(file_path, 'wb') as f:
				f.write(text.encode('utf-8'))
			print(f"✓ 已修复: {file_path}")
			return True
		else:
			print(f"- 无需修复: {file_path}")
			return True

	except Exception as e:
		print(f"✗ 处理文件失败 {file_path}: {e}")
		return False

def main():
	base_dir = os.path.dirname(os.path.abspath(__file__))
	os.chdir(base_dir)

	print("开始修复Java文件编码问题...\n")

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
