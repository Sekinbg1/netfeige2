#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Read the file
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'r') as f:
	lines = f.readlines()

# Find and replace the import line
new_lines = []
for line in lines:
	if line.strip() == 'import importlib.util as imp':
		new_lines.append('try:\n')
		new_lines.append('    import imp\n')
		new_lines.append('except ImportError:\n')
		new_lines.append('	# Python 3.12+ compatibility\n')
		new_lines.append('	import importlib.util\n')
		new_lines.append('	import importlib.machinery\n')
		new_lines.append('	\n')
		new_lines.append('	class ImpCompat:\n')
		new_lines.append('		@staticmethod\n')
		new_lines.append('		def find_module(name, path=None):\n')
		new_lines.append('			if path:\n')
		new_lines.append('				spec = importlib.util.find_spec(name, path[0] if path else None)\n')
		new_lines.append('			else:\n')
		new_lines.append('				spec = importlib.util.find_spec(name)\n')
		new_lines.append('			if spec is None:\n')
		new_lines.append("				raise ModuleNotFoundError(f\"No module named '{name}'\")\n")
		new_lines.append("			return None, spec.origin, ('', '', importlib.machinery.ExtensionFileLoader)\n")
		new_lines.append('		\n')
		new_lines.append('		@staticmethod\n')
		new_lines.append('		def load_module(name, file, pathname, desc):\n')
		new_lines.append('			spec = importlib.util.spec_from_file_location(name, pathname)\n')
		new_lines.append('			module = importlib.util.module_from_spec(spec)\n')
		new_lines.append('			spec.loader.exec_module(module)\n')
		new_lines.append('			return module\n')
		new_lines.append('	\n')
		new_lines.append('	imp = ImpCompat()\n')
	else:
		new_lines.append(line)

# Write back
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'w') as f:
	f.writelines(new_lines)

print('Fixed Build.scons for Python 3.12+ compatibility!')
