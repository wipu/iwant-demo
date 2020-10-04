package org.fluentjava.iwant.entry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Iwant {

	private static final boolean DEBUG_LOG = "a".contains("b");

	private static final File HOME = new File(System.getProperty("user.home"));

	public static final File IWANT_USER_DIR = new File(HOME,
			".org.fluentjava.iwant");

	public static final String EXAMPLE_COMMIT = "f68535c89288af153156e7ac00e90936dd773712";

	public static final String EXAMPLE_IWANT_FROM_CONTENT = ""
			+ "# uncomment and optionally change the commit:\n"
			+ "# (also note the content of the url is assumed unmodifiable so it's downloaded only once)\n"
			+ "#iwant-from=https://github.com/wipu/iwant/archive/"
			+ EXAMPLE_COMMIT + ".zip\n";

	static {
		mkdirs(IWANT_USER_DIR);
	}

	private final IwantNetwork network;

	protected Iwant(IwantNetwork network) {
		this.network = network;
	}

	/**
	 * TODO rename, this is not only about network
	 */
	public interface IwantNetwork {

		File cacheOfContentFrom(UnmodifiableSource<?> src);

		JavaCompiler systemJavaCompiler();

	}

	public static abstract class UnmodifiableSource<T> {

		private final T location;

		public UnmodifiableSource(T location) {
			this.location = location;
		}

		public final String rawLocationString() {
			return location.toString();
		}

		@Override
		public final String toString() {
			return getClass().getSimpleName() + ":" + rawLocationString();
		}

		public final T location() {
			return location;
		}

		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			return result;
		}

		@Override
		public final boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnmodifiableSource<?> other = (UnmodifiableSource<?>) obj;
			if (location == null) {
				if (other.location != null) {
					return false;
				}
			} else if (!location.equals(other.location)) {
				return false;
			}
			return true;
		}

	}

	public static class UnmodifiableUrl extends UnmodifiableSource<URL> {

		public UnmodifiableUrl(URL location) {
			super(location);
		}

	}

	public static class UnmodifiableZip extends UnmodifiableSource<URL> {

		public UnmodifiableZip(URL location) {
			super(location);
		}

	}

	public static class UnmodifiableIwantBootstrapperClassesFromIwantWsRoot
			extends UnmodifiableSource<URL> {

		public UnmodifiableIwantBootstrapperClassesFromIwantWsRoot(
				File iwantEssential) {
			super(fileToUrl(iwantEssential));
		}

	}

	public static URL fileToUrl(File file) {
		try {
			URL url = file.toURI().toURL();
			String urlString = url.toExternalForm();
			url = new URL(withoutTrailingSlash(urlString));
			return url;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static URL withTrailingSlashIfDir(URL url) {
		try {
			// here we trust this is only used for file urls
			File asFile = new File(url.toURI());
			if (!asFile.isDirectory()) {
				return url;
			}
			String urlString = url.toExternalForm();
			if (urlString.endsWith("/")) {
				return url;
			}
			return new URL(urlString + "/");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static class RealIwantNetwork implements IwantNetwork {

		@Override
		public File cacheOfContentFrom(UnmodifiableSource<?> src) {
			File cached = new File(IWANT_USER_DIR, "cached");
			File cachedFromSrc = new File(cached,
					src.getClass().getSimpleName());
			String fileName = toSafeFilename(src.rawLocationString());
			return new File(cachedFromSrc, fileName);
		}

		@Override
		public JavaCompiler systemJavaCompiler() {
			return ToolProvider.getSystemJavaCompiler();
		}

	}

	public static URL url(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Iwant using(IwantNetwork network) {
		return new Iwant(network);
	}

	public static Iwant usingRealNetwork() {
		return new Iwant(new RealIwantNetwork());
	}

	public IwantNetwork network() {
		return network;
	}

	public static void main(String[] args) throws Exception {
		try {
			usingRealNetwork().evaluate(args);
		} catch (IwantException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public static class IwantException extends RuntimeException {

		public IwantException(String message) {
			super(message);
		}

	}

	public void evaluate(String... args) throws Exception {
		if (args.length <= 0) {
			throw new IwantException("Usage: " + Iwant.class.getCanonicalName()
					+ " AS_SOMEONE_DIRECTORY [args...]");
		}
		File asSomeone = new File(args[0]);
		if (!asSomeone.exists()) {
			throw new IwantException("AS_SOMEONE_DIRECTORY does not exist: "
					+ asSomeone.getCanonicalPath());
		}
		File iwantSrc = iwantSourceOfWishedVersion(asSomeone);
		File iwantEssential = new File(iwantSrc, "essential");

		File iwantBootstrapClasses = iwantBootstrapperClasses(iwantEssential);

		String[] iwant2Args = new String[args.length + 1];
		iwant2Args[0] = iwantEssential.getCanonicalPath();
		System.arraycopy(args, 0, iwant2Args, 1, args.length);

		runJavaMain(false, true, "org.fluentjava.iwant.entry2.Iwant2",
				Arrays.asList(iwantBootstrapClasses), iwant2Args);
	}

	public static URL wishedIwantRootFromUrl(File asSomeone) {
		return wishedIwantFromProperty(asSomeone, "iwant-from", Iwant::url);
	}

	private static <TYPE> TYPE wishedIwantFromProperty(File asSomeone,
			String propertyName, Function<String, TYPE> parser) {
		try {
			Properties iwantFromProps = iwantFromProperties(asSomeone);
			String value = iwantFromProps.getProperty(propertyName);
			if (value == null) {
				throw new IwantException("Please define '" + propertyName
						+ "' in " + iwantFromFile(asSomeone) + "\nExample:\n"
						+ EXAMPLE_IWANT_FROM_CONTENT);
			}
			try {
				return parser.apply(value);
			} catch (Exception e) {
				throw new IwantException(e.getMessage()
						+ "\nPlease define a valid '" + propertyName + "' in "
						+ iwantFromFile(asSomeone) + "\nExample:\n"
						+ EXAMPLE_IWANT_FROM_CONTENT);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public File iwantSourceOfWishedVersion(File asSomeone) {
		File zip = iwantSourceZipOfWishedVersion(asSomeone);
		File zipUnzipped = unmodifiableZipUnzipped(
				new UnmodifiableZip(fileToUrl(zip)));
		return theSoleChildOf(zipUnzipped);
	}

	public static File theSoleChildOf(File dir) {
		File[] children = dir.listFiles();
		if (children == null || children.length != 1) {
			throw new IwantException(
					"The file is not a directory with exactly one child: "
							+ dir);
		}
		return children[0];
	}

	public File iwantSourceZipOfWishedVersion(File asSomeone) {
		try {
			URL iwantRootUrl = wishedIwantRootFromUrl(asSomeone);
			return downloaded(iwantRootUrl);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static File iwantFromFile(File asSomeone) {
		return new File(asSomeone, "i-have/conf/iwant-from");
	}

	private static Properties iwantFromProperties(File asSomeone)
			throws IOException, FileNotFoundException {
		File iwantFrom = iwantFromFile(asSomeone);
		File iwantFromParent = iwantFrom.getParentFile();
		if (!iwantFromParent.exists()) {
			mkdirs(iwantFromParent);
		}
		if (!iwantFrom.exists()) {
			newTextFile(iwantFrom, EXAMPLE_IWANT_FROM_CONTENT);
			throw new IwantException("I created " + iwantFrom
					+ "\nPlease edit and uncomment the properties in it and rerun me.");
		}
		Properties iwantFromProps = new Properties();
		try (FileReader fr = new FileReader(iwantFrom)) {
			iwantFromProps.load(fr);
		}
		return iwantFromProps;
	}

	private static File tryToWriteTextFile(File file, String content)
			throws IOException {
		mkdirs(file.getParentFile());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.append(content);
			return file;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static File newTextFile(File file, String content) {
		try {
			return tryToWriteTextFile(file, content);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private File iwantBootstrapperClasses(File iwantEssential) {
		File classes = network.cacheOfContentFrom(
				new UnmodifiableIwantBootstrapperClassesFromIwantWsRoot(
						iwantEssential));
		List<File> javaSrcs = iwantBootstrappingJavaSources(iwantEssential);
		if (bootstrapperIngredientsChanged(classes, javaSrcs)) {
			compiledClasses(classes,
					iwantBootstrappingJavaSources(iwantEssential),
					Collections.<File> emptyList(), bootstrappingJavacOptions(),
					StandardCharsets.UTF_8);
		}
		return classes;
	}

	private static boolean bootstrapperIngredientsChanged(File target,
			List<File> srcDeps) {
		if (!target.exists()) {
			return true;
		}
		return isModifiedSince(srcDeps, target.lastModified());
	}

	public static boolean isModifiedSince(File src, long time) {
		if (src.lastModified() >= time) {
			return true;
		}
		if (src.isDirectory()) {
			for (File child : src.listFiles()) {
				if (isModifiedSince(child, time)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isModifiedSince(List<File> srcs, long time) {
		for (File src : srcs) {
			if (isModifiedSince(src, time))
				return true;
		}
		return false;
	}

	public static List<String> bootstrappingJavacOptions() {
		List<String> options = new ArrayList<>();
		options.addAll(recommendedJavacWarningOptions());
		options.add("-g");
		return options;
	}

	public static List<String> recommendedJavacWarningOptions() {
		return Arrays.asList("-Xlint", "-Xlint:-serial");
	}

	public File compiledClasses(File dest, List<File> src,
			List<File> classLocations, List<String> javacOptions,
			Charset encoding) {
		try {
			StringBuilder cp = new StringBuilder();
			for (Iterator<File> iterator = classLocations.iterator(); iterator
					.hasNext();) {
				File classLocation = iterator.next();
				cp.append(classLocation.getCanonicalPath());
				if (iterator.hasNext()) {
					cp.append(pathSeparator());
				}
			}
			return compiledClasses(dest, src, cp.toString(), javacOptions,
					encoding);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public File compiledClasses(File dest, List<File> src, String classpath,
			List<String> javacOptions, Charset encoding) {
		try {
			debugLog("compiledClasses", "dest: " + dest, "src: " + src,
					"classpath: " + classpath, "javacOptions:" + javacOptions);
			del(dest);
			mkdirs(dest);
			JavaCompiler compiler = network.systemJavaCompiler();
			if (compiler == null) {
				throw new IwantException(
						"Cannot find system java compiler. Are you running a JRE instead of JDK?");
			}
			DiagnosticListener<? super JavaFileObject> diagnosticListener = null;
			Locale locale = null;
			StandardJavaFileManager fileManager = compiler
					.getStandardFileManager(diagnosticListener, locale,
							encoding);
			Iterable<? extends JavaFileObject> compilationUnits = fileManager
					.getJavaFileObjectsFromFiles(src);
			Writer compilerTaskOut = null;
			Iterable<String> classes = null;

			List<String> options = new ArrayList<>();
			options.addAll(javacOptions);
			options.add("-d");
			options.add(dest.getCanonicalPath());
			options.add("-classpath");
			options.add(classpath);
			options.add("-encoding");
			options.add(encoding.toString());

			CompilationTask compilerTask = compiler.getTask(compilerTaskOut,
					fileManager, diagnosticListener, options, classes,
					compilationUnits);
			Boolean compilerTaskResult = compilerTask.call();
			fileManager.close();
			if (!compilerTaskResult) {
				throw new IwantException("Compilation failed.");
			}
			return dest;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static char pathSeparator() {
		return File.pathSeparatorChar;
	}

	public static void debugLog(String task, Object... lines) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			b.append(String.format("(%16s    ", task));
			b.append(lines[i]);
			b.append(")");
			if (i < lines.length - 1) {
				b.append("\n");
			}
		}
		fileLog(b.toString());
		if (DEBUG_LOG) {
			System.err.println(b);
		}
	}

	public static void log(String task, File target) {
		StringBuilder b = new StringBuilder();
		b.append(String.format(":%16s -> ", task));
		b.append(target);
		System.err.println(b);
		fileLog(b.toString());
	}

	private static List<File> iwantBootstrappingJavaSources(
			File iwantEssential) {
		File iwant2 = new File(iwantEssential,
				"iwant-entry2/src/main/java/org/fluentjava/iwant/entry2/Iwant2.java");
		File iwant = new File(iwantEssential,
				"iwant-entry/as-some-developer/with/java/org/fluentjava/iwant/entry/Iwant.java");
		return Arrays.asList(iwant2, iwant);
	}

	private static class StreamsAndSecurityManager {

		private final PrintStream out;
		private final PrintStream err;
		private final SecurityManager securityManager;

		StreamsAndSecurityManager(PrintStream out, PrintStream err,
				SecurityManager securityManager) {
			this.out = out;
			this.err = err;
			this.securityManager = securityManager;
		}

	}

	private static StreamsAndSecurityManager originalStreamsAndSecurityManager;
	private static int catchStreamsAndSystemExitsRequestCount = 0;

	private static synchronized void catchStreamsAndSystemExits() {
		catchStreamsAndSystemExitsRequestCount++;
		if (originalStreamsAndSecurityManager != null) {
			// already handled
			return;
		}
		originalStreamsAndSecurityManager = new StreamsAndSecurityManager(
				System.out, System.err, System.getSecurityManager());
		System.setOut(System.err);
		System.setSecurityManager(new ExitCatcher());
	}

	private static synchronized void restoreOriginalStreamsAndSecurityManager() {
		catchStreamsAndSystemExitsRequestCount--;
		if (originalStreamsAndSecurityManager != null
				&& catchStreamsAndSystemExitsRequestCount <= 0) {
			System.setOut(originalStreamsAndSecurityManager.out);
			System.setErr(originalStreamsAndSecurityManager.err);
			System.setSecurityManager(
					originalStreamsAndSecurityManager.securityManager);
			originalStreamsAndSecurityManager = null;
		}
	}

	public static void runJavaMain(boolean catchPrintsAndSystemExit,
			boolean hideIwantClasses, String className,
			List<File> classLocations, String... args) throws Exception {
		debugLog("runJavaMain", "class: " + className,
				"args: " + Arrays.toString(args),
				"catchPrintsAndSystemExit=" + catchPrintsAndSystemExit,
				"hideIwantClasses=" + hideIwantClasses,
				"classLocations: " + classLocations);
		ClassLoader classLoader = classLoader(hideIwantClasses, classLocations);
		Class<?> mainClass = classLoader.loadClass(className);
		Method mainMethod = mainClass.getMethod("main", String[].class);

		Object[] invocationArgs = { args };

		if (catchPrintsAndSystemExit) {
			catchStreamsAndSystemExits();
		}
		try {
			mainMethod.invoke(null, invocationArgs);
		} catch (ExitCalledException e) {
			if (e.status() != 0) {
				throw new IwantException(
						className + " exited with " + e.status());
			}
		} finally {
			if (catchPrintsAndSystemExit) {
				restoreOriginalStreamsAndSecurityManager();
			}
		}
	}

	public static class ExitCalledException extends SecurityException {

		private final int status;

		public ExitCalledException(int status) {
			this.status = status;
		}

		public int status() {
			return status;
		}

	}

	public static class ExitCatcher extends SecurityManager {

		@Override
		public void checkPermission(Permission perm) {
			// everything allowed
		}

		@Override
		public void checkExit(int status) {
			throw new ExitCalledException(status);
		}

	}

	/**
	 * This forces the second phase of bootstrapping to use its own versions of
	 * any iwant classes so we are free to make changes to this very file
	 * without breaking things.
	 */
	private static class ClassLoaderThatHidesIwant extends ClassLoader {

		@Override
		protected synchronized Class<?> loadClass(String name, boolean resolve)
				throws ClassNotFoundException {
			if (isClassnameToHide(name)) {
				return null;
			} else {
				return super.loadClass(name, resolve);
			}
		}

		private static boolean isClassnameToHide(String name) {
			if (name.startsWith("org.fluentjava.iwant")) {
				return isIwantClassnameToHide(name);
			}
			return false;
		}

		private static boolean isIwantClassnameToHide(String name) {
			// canonical name of inner classes is not compatible with
			// classloading (!!) so this manual name tweaking is needed:
			if ((Iwant.class.getCanonicalName() + "$"
					+ ExitCalledException.class.getSimpleName()).equals(name)) {
				// this is an exceptional case, for catches to work
				return false;
			}
			return true;
		}

	}

	public static ClassLoader classLoader(boolean hideIwantClasses,
			List<File> locations) {
		ClassLoader parent = hideIwantClasses ? new ClassLoaderThatHidesIwant()
				: null;
		return classLoader(parent, locations);
	}

	public static ClassLoader classLoader(ClassLoader parent,
			List<File> locations) {
		URL[] urls = new URL[locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			File location = locations.get(i);
			URL asUrl = fileToUrl(location);
			// TODO own type so we don't need to slash back and forth
			asUrl = withTrailingSlashIfDir(asUrl);
			urls[i] = asUrl;
		}
		if (parent != null) {
			return new URLClassLoader(urls, parent);
		} else {
			return new URLClassLoader(urls);
		}
	}

	public static void fileLog(String msg) {
		try (FileWriter fw = new FileWriter(new File(IWANT_USER_DIR, "log"),
				true)) {
			fw.append(new Date().toString()).append(" - ").append(msg)
					.append("\n");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static final String encSlash = urlEncode("/");
	private static final String encQuestion = urlEncode("?");

	public static String toSafeFilename(String from) {
		// first make sure we talk unix separators here
		String to = from.replaceAll(fileSeparatorRegex(), "/");

		// then the mangling
		to = urlEncode(to);
		to = to.replaceAll(encSlash, "/");
		// starting slash
		to = to.replaceAll("^/", encSlash);
		// parent dir refs
		to = to.replaceAll("/\\.\\.", encSlash + "..");
		to = to.replaceAll("\\.\\./", ".." + encSlash);
		// repeating slashes
		to = to.replaceAll("//", "/" + encSlash);
		// url query
		to = to.replaceAll(encQuestion, "?");

		// finally convert back to Windows, if that's the case
		to = to.replaceAll("/", fileSeparatorRegex());
		return to;
	}

	private static String fileSeparatorRegex() {
		String sep = System.getProperty("file.separator");
		return "\\".equals(sep) ? "\\\\" : sep;
	}

	private static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static synchronized void del(File file) {
		if (!file.exists()) {
			// it may be a broken symlink so let's try delete anyway
			if (!file.delete()) {
				debugLog("del", "Failed to delete 'non-existing' " + file);
			}
			return; // nothing more to do
		}
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				del(child);
			}
		}
		if (!file.delete()) {
			throw new IwantException("del failed for " + file);
		}
	}

	public void downloaded(URL from, File to) {
		try {
			if (to.exists()) {
				return;
			}
			mkdirs(to.getParentFile());
			debugLog("Downloading", "from " + from);
			log("Downloading", to);
			byte[] bytes = downloadBytes(from);
			FileOutputStream cachedOut = new FileOutputStream(to);
			cachedOut.write(bytes);
			cachedOut.close();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public File downloaded(URL from) {
		File to = network.cacheOfContentFrom(new UnmodifiableUrl(from));
		downloaded(from, to);
		return to;
	}

	private static byte[] downloadBytes(URL url)
			throws MalformedURLException, IOException {
		enableHttpProxy();
		URLConnection conn = url.openConnection();
		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			int status = httpConn.getResponseCode();
			if (isRedirect(status)) {
				String location = conn.getHeaderField("Location");
				httpConn.disconnect();
				return downloadBytes(new URL(location));
			}
		}
		InputStream in = conn.getInputStream();
		byte[] respBody = readBytes(in);
		in.close();
		return respBody;
	}

	private static boolean isRedirect(int status) {
		return 300 <= status && status < 400;
	}

	private static void enableHttpProxy() {
		unixHttpProxyToJavaHttpProxy(System.getenv("http_proxy"),
				System.getenv("https_proxy"));
	}

	public static void unixHttpProxyToJavaHttpProxy(String httpProxy,
			String httpsProxy) {
		proxyUrlToJava(httpProxy, "http");
		proxyUrlToJava(httpsProxy, "https");
	}

	private static void proxyUrlToJava(String urlString, String prefix) {
		if (urlString == null || "".equals(urlString)) {
			return;
		}
		URL url = url(urlString);
		String host = url.getHost();
		int port = url.getPort();
		System.setProperty(prefix + ".proxyHost", host);
		if (port >= 0) {
			System.setProperty(prefix + ".proxyPort", Integer.toString(port));
		}
	}

	private static byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream body = new ByteArrayOutputStream();
		while (true) {
			int i = in.read();
			if (i < 0) {
				break;
			}
			byte b = (byte) i;
			body.write(b);
		}
		return body.toByteArray();
	}

	public File unmodifiableZipUnzipped(UnmodifiableZip src) {
		try {
			File dest = network.cacheOfContentFrom(src);
			if (dest.exists()) {
				return dest;
			}
			log("Unzipping", dest);
			File tmp = new File(dest + ".tmp");
			del(tmp);
			mkdirs(tmp);
			ZipInputStream zip = new ZipInputStream(
					src.location().openStream());
			ZipEntry e = null;
			byte[] buffer = new byte[32 * 1024];
			boolean zipHasContent = false;
			while ((e = zip.getNextEntry()) != null) {
				zipHasContent = true;
				File entryFile = new File(tmp, e.getName());
				if (e.isDirectory()) {
					mkdirs(entryFile);
					continue;
				}
				OutputStream out = new FileOutputStream(entryFile);
				while (true) {
					int bytesRead = zip.read(buffer);
					if (bytesRead <= 0) {
						break;
					}
					out.write(buffer, 0, bytesRead);
				}
				out.close();
			}
			zip.close();
			if (!zipHasContent) {
				throw new IwantException(
						"Corrupt (or empty, no way to tell): " + src);
			}
			fileRenamedTo(tmp, dest);
			return dest;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void fileRenamedTo(File src, File dest) {
		if (!src.renameTo(dest)) {
			throw new IwantException("Failed to rename " + src + " to " + dest);
		}
	}

	public static String withoutTrailingSlash(String string) {
		if (!string.endsWith("/")) {
			return string;
		}
		return string.substring(0, string.length() - 1);
	}

	public static synchronized void mkdirs(File dir) {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IwantException(
						"mkdirs failed for existing non-directory " + dir);
			}
			return;
		}
		if (!dir.mkdirs()) {
			throw new IwantException("mkdirs failed for " + dir);
		}
	}
}
