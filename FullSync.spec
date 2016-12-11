#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor,
# Boston, MA 02110-1301, USA.
#

%define java_version 1.8.0
%define gtk_version 2.4.1
# disbale debuginfo subpackage generation - there is nothing here that has debuginfos
%global debug_package %{nil}

Name:           FullSync
Version:        0.10.4
Release:        0
Summary:        Easy file synchronization for everyone
License:        GPL-2.0+
Group:          Productivity/Archiving/Backup
URL:            https://fullsync.sourceforge.io/
Source0:        %{name}-%{version}-src.tar.gz
AutoReqProv:    no

%ifarch x86_64
%if 0%{?fedora}%{defined el7} > 0
Requires:       jre >= %{java_version}
%else
Requires:       jre-64 >= %{java_version}
%endif
%else
Requires:       jre >= %{java_version}
%endif

%if 0%{?fedora}%{defined el7} > 0
Requires:       gtk2%{_isa} >= %{gtk_version}
%else
Requires:       libgtk-2_0-0%{_isa} >= %{gtk_version}
%endif
Requires:       xdg-utils
Requires:       xdg-user-dirs
Requires:       desktop-file-utils
BuildRequires:  ant
BuildRequires:  java-devel >= %{java_version}
BuildRequires:  hicolor-icon-theme
BuildRequires:  desktop-file-utils
BuildRequires:  dos2unix
ExclusiveArch:  x86_64 i386 i486 i586 i686
BuildRoot:      %{_tmppath}/%{name}-%{version}-build

%define fsdir %{_javadir}/%{name}-%{version}
# disbale debuginfo subpackage generation - there is nothing here that has debuginfos
%global debug_package %{nil}
%define exename fullsync
%define icondir %{_datadir}/icons/hicolor/scalable/apps/

%description
FullSync is a universal file synchronization and backup tool
which is highly customizable and expandable. It is especially
for developers, but the basic functionality is easy enough for everyone.

%prep
%setup -q -n %{name}-%{version}

%build
export CLASSPATH=
export OPT_JAR_LIST=:
ant build
desktop-file-edit "--set-key=Exec" "--set-value=%{_bindir}/%{exename}" "build/%{exename}.desktop"
desktop-file-edit "--set-key=Comment" "--set-value=Easy file synchronization for everyone. Version: %{version}" "build/%{exename}.desktop"

%install
cd build/
dos2unix fullsync
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/lib/
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/images/
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/versions/
install -d -m 755 $RPM_BUILD_ROOT%{icondir}
install -d -m 755 $RPM_BUILD_ROOT%{_datadir}/applications
install -d -m 755 $RPM_BUILD_ROOT%{_bindir}/
install -m 644 launcher.jar $RPM_BUILD_ROOT%{fsdir}/launcher.jar
install -m 644 LICENSE $RPM_BUILD_ROOT%{fsdir}/LICENSE
install -m 644 ChangeLog.txt $RPM_BUILD_ROOT%{fsdir}/ChangeLog.txt
install -m 755 %{exename} $RPM_BUILD_ROOT%{fsdir}/%{exename}
ln -s %{fsdir}/%{exename} $RPM_BUILD_ROOT%{_bindir}/%{exename}
install -m 644 versions/*.html $RPM_BUILD_ROOT%{fsdir}/versions/
install -m 644 lib/*.jar $RPM_BUILD_ROOT%{fsdir}/lib/
rm -rf $RPM_BUILD_ROOT%{fsdir}/lib/swt-*
%ifarch x86_64
install -m 644 lib/swt-gtk-linux-x86_64.jar $RPM_BUILD_ROOT%{fsdir}/lib/swt-gtk-linux-x86_64.jar
%else
install -m 644 lib/swt-gtk-linux-x86.jar $RPM_BUILD_ROOT%{fsdir}/lib/swt-gtk-linux-x86.jar
%endif
install -m 644 %{exename}.svg $RPM_BUILD_ROOT%{icondir}/%{exename}.svg
install -m 644 %{exename}.desktop $RPM_BUILD_ROOT%{_datadir}/applications/%{exename}.desktop
install -m 644 images/* $RPM_BUILD_ROOT%{fsdir}/images/


%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(0644,root,root,0755)
%dir %{fsdir}/
%dir %{fsdir}/lib
%dir %{fsdir}/images
%dir %{fsdir}/versions
%doc %{fsdir}/LICENSE
%doc %{fsdir}/ChangeLog.txt
%{fsdir}/launcher.jar
%attr(0755 root root) %{fsdir}/%{exename}
%{_bindir}/%{exename}
%{fsdir}/lib/*.jar
%{fsdir}/images/*
%{fsdir}/versions/*.html
%{_datadir}/applications/%{exename}.desktop
%{icondir}/%{exename}.svg

%post
%if 0%{?fedora}%{defined el7} > 0
/usr/bin/update-desktop-database &> /dev/null || :
touch --no-create %{_datadir}/icons/hicolor/ &>/dev/null || :
%else
%desktop_database_post
%endif
exit 0

%postun
%if 0%{?fedora}%{defined el7} > 0
/usr/bin/update-desktop-database &> /dev/null || :
if [ $1 -eq 0 ]; then
	touch --no-create %{_datadir}/icons/hicolor/ &>/dev/null || :
	gtk-update-icon-cache %{_datadir}/icons/hicolor/ &>/dev/null || :
fi
%else
%desktop_database_postun
%endif
exit 0

%posttrans
%if 0%{?fedora}%{defined el7} > 0
gtk-update-icon-cache %{_datadir}/icons/hicolor/ &>/dev/null || :
%endif
exit 0

%changelog
