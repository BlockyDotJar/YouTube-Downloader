[Setup]
AppName=YouTube-Downloader Wizard
AppVersion=3.0.0
AppVerName=YouTube-Downloader Wizard {#SetupSetting('AppVersion')}
DefaultDirName={commonpf32}\YouTube-Downloader
DefaultGroupName=YouTube-Downloader
OutputDir="D:\YT-Downloader-Builds"
SourceDir={#SetupSetting('OutputDir')}
OutputBaseFilename=YouTube-Downloader-Setup-{#SetupSetting('AppVersion')}
ChangesAssociations=yes

[InstallDelete]
Type: filesandordirs; Name: "{app}\update"

[Dirs]
Name: "{app}"
Name: "{app}\Downloads"; Flags: uninsneveruninstall

[Files]
Source: "LICENSE"; DestDir: "{app}"; Flags: ignoreversion
Source: "yt-downloader.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "ffmpeg.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "yt-dlp.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "icon.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "settings.json"; DestDir: "{app}"; Flags: uninsneveruninstall
Source: "installation-wizard.bmp"; DestDir: "{tmp}"; Flags: dontcopy

[Icons]
Name: "{commondesktop}\YouTube-Downloader"; Filename: "{app}\yt-downloader.exe"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon; Check: ShouldCreateDesktopShortcut
Name: "{group}\YouTube-Downloader"; Filename: "{app}\yt-downloader.exe"; IconFilename: "{app}\icon.ico"

[Registry]
Root: HKLM; Subkey: "SOFTWARE\YouTube-Downloader"; ValueType: string; ValueName: "YouTube-Downloader_HOME"; ValueData: "{app}"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\YouTube-Downloader"; ValueType: string; ValueName: "YouTube-Downloader_Version"; ValueData: "{#SetupSetting('AppVersion')}"; Flags: uninsdeletevalue

[Run]
Filename: "{app}\yt-downloader.exe"; Tasks: opendownloader; Check: ShouldOpenDownloader

[Code]
var
  CustomPage: TWizardPage;
  Image: TBitmapImage;
  BitmapFilePath: String;
  ShouldCreateShortcut: Boolean;
  ShouldOpenDownloaderApplication: Boolean;

function InitializeSetup(): Boolean;
begin
  ShouldCreateShortcut := True;
  ShouldOpenDownloaderApplication := True;
  Result := True;
end;

procedure InitializeWizard;
begin
  CustomPage := CreateCustomPage(wpWelcome, 'Installation wizard', 'Let the installation wizard install this software for you...');

  Image := TBitmapImage.Create(WizardForm);
  Image.Parent := CustomPage.Surface;

  try
    ExtractTemporaryFile('installation-wizard.bmp');
    BitmapFilePath := ExpandConstant('{tmp}\installation-wizard.bmp');
   
    Image.Bitmap.LoadFromFile(BitmapFilePath);
    Image.Width := Image.Bitmap.Width;
    Image.Height := Image.Bitmap.Height;

    Image.Left := (CustomPage.SurfaceWidth - Image.Width) div 2;
    Image.Top := (CustomPage.SurfaceHeight - Image.Height) div 2;
  except
    MsgBox('Error loading installation-wizard.bmp', mbError, MB_OK);
  end;
end;

procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpSelectTasks then
  begin
    WizardForm.TasksList.Checked[0] := ShouldCreateShortcut;
    WizardForm.TasksList.Checked[1] := ShouldOpenDownloaderApplication;
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    ShouldCreateShortcut := WizardForm.TasksList.Checked[0];
    ShouldOpenDownloaderApplication := WizardForm.TasksList.Checked[1];
  end;
end;

function ShouldCreateDesktopShortcut(): Boolean;
begin
  Result := ShouldCreateShortcut;
end;

function ShouldOpenDownloader(): Boolean;
begin
  Result := ShouldOpenDownloaderApplication;
end;

[Tasks]
Name: "desktopicon"; Description: "Create a desktop icon"; Flags: unchecked
Name: "opendownloader"; Description: "Open YouTube-Downloader after installation"; Flags: unchecked
