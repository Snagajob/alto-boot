import sys
import time
import os
import logging
import shutil
import errno
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

if __name__ == "__main__":
	"""
	Update target directory on file change
	Sample usage: python scripts/watch-ui.py '/Users/zach.mcgonigal/snagajob/alto-boot/src/main/resources' '/Users/zach.mcgonigal/snagajob/alto-boot/build/resources/main/'
	"""
	class Handler(FileSystemEventHandler):
		def on_modified(self, event):
			path = sys.argv[1] if len(sys.argv) > 1 else '.'
			dest = sys.argv[2]
			self.update_target(path, dest)

		def update_target(self, src, dst):
			try:
				shutil.rmtree(dst)
				shutil.copytree(src, dst)
			except OSError as exc: # python >2.5
				if exc.errno == errno.ENOTDIR:
					shutil.copy2(src, dst)
				else:
					raise

	path = sys.argv[1] if len(sys.argv) > 1 else '.'
	event_handler = Handler()
	observer = Observer()
	observer.schedule(event_handler, path, recursive=True)
	observer.start()
	try:
		while True:
			time.sleep(1)
	except KeyboardInterrupt:
		observer.stop()
	observer.join()
