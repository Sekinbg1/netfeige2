#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import re

# Read the file
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'r') as f:
	lines = f.readlines()

# Find and replace the imp compatibility block
new_lines = []
skip_until_from_glob = False

for i, line in enumerate(lines):
	if line.strip() == 'try:' and i < len(lines) - 1 and 'import imp' in lines[i+1]:
		# Start of imp block - replace it
		skip_until_from_glob = True
		new_lines.append('try:\n')
		new_lines.append('	import imp\n')
		new_lines.append('except ImportError:\n')
		new_lines.append('	# Python 3.12+ compatibility\n')
		new_lines.append('	import importlib.util\n')
		new_lines.append('	import importlib.machinery\n')
		new_lines.append('	import sys\n')
		new_lines.append('	import os\n')
		new_lines.append('	\n')
		new_lines.append('	class ImpCompat:\n')
		new_lines.append('		@staticmethod\n')
		new_lines.append('		def find_module(name, path=None):\n')
		new_lines.append('			# Try to find .py or .scons file in the given path\n')
		new_lines.append('			if path:\n')
		new_lines.append('				for p in path:\n')
		new_lines.append("					py_file = os.path.join(p, name + '.py')\n")
		new_lines.append("					scons_file = os.path.join(p, name + '.scons')\n")
		new_lines.append('					if os.path.exists(py_file):\n')
		new_lines.append("						return None, py_file, ('.py', 'U', 1)\n")
		new_lines.append('					elif os.path.exists(scons_file):\n')
		new_lines.append("						return None, scons_file, ('.scons', 'U', 1)\n")
		new_lines.append('			raise ModuleNotFoundError(f"No module named \'{name}\'")\n')
		new_lines.append('		\n')
		new_lines.append('		@staticmethod\n')
		new_lines.append('		def load_module(name, file, pathname, desc):\n')
		new_lines.append('			# Create a module with a valid Python identifier name\n')
		new_lines.append("			safe_name = name.replace('-', '_')\n")
		new_lines.append('			spec = importlib.util.spec_from_file_location(safe_name, pathname)\n')
		new_lines.append('			module = importlib.util.module_from_spec(spec)\n')
		new_lines.append('			sys.modules[safe_name] = module\n')
		new_lines.append('			spec.loader.exec_module(module)\n')
		new_lines.append('			return module\n')
		new_lines.append('	\n')
		new_lines.append('	imp = ImpCompat()\n')
	elif skip_until_from_glob:
		if line.strip().startswith('from glob import glob'):
			skip_until_from_glob = False
			new_lines.append(line)
	else:
		new_lines.append(line)

# Write back
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'w') as f:
	f.writelines(new_lines)

print('Successfully updated imp compatibility layer!')
