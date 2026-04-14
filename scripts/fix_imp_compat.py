#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os

# Read the file
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'r') as f:
	content = f.read()

# Find and replace the ImpCompat class
old_imp_compat = '''try:
	import imp
except ImportError:
	# Python 3.12+ compatibility
	import importlib.util
	import importlib.machinery

	class ImpCompat:
		@staticmethod
		def find_module(name, path=None):
			if path:
				spec = importlib.util.find_spec(name, path[0] if path else None)
			else:
				spec = importlib.util.find_spec(name)
			if spec is None:
				raise ModuleNotFoundError(f"No module named '{name}'")
			return None, spec.origin, ('', '', importlib.machinery.ExtensionFileLoader)

		@staticmethod
		def load_module(name, file, pathname, desc):
			spec = importlib.util.spec_from_file_location(name, pathname)
			module = importlib.util.module_from_spec(spec)
			spec.loader.exec_module(module)
			return module

	imp = ImpCompat()'''

new_imp_compat = '''try:
	import imp
except ImportError:
	# Python 3.12+ compatibility
	import importlib.util
	import importlib.machinery
	import sys

	class ImpCompat:
		@staticmethod
		def find_module(name, path=None):
			# Try to find .py or .scons file in the given path
			if path:
				for p in path:
					py_file = os.path.join(p, name + '.py')
					scons_file = os.path.join(p, name + '.scons')
					if os.path.exists(py_file):
						return None, py_file, ('.py', 'U', 1)
					elif os.path.exists(scons_file):
						return None, scons_file, ('.scons', 'U', 1)
			raise ModuleNotFoundError(f"No module named '{name}'")

		@staticmethod
		def load_module(name, file, pathname, desc):
			# Create a module with a valid Python identifier name
			safe_name = name.replace('-', '_')
			spec = importlib.util.spec_from_file_location(safe_name, pathname)
			module = importlib.util.module_from_spec(spec)
			sys.modules[safe_name] = module
			spec.loader.exec_module(module)
			return module

	imp = ImpCompat()'''

content = content.replace(old_imp_compat, new_imp_compat)

# Write back
with open('/home/lina/platinum-build/Platinum/Build/Build.scons', 'w') as f:
	f.write(content)

print('Fixed imp compatibility layer!')
