# BrainOps Weather - APK Signing Documentation

## Keystore Information

- **Keystore File**: `brainops-release.jks`
- **Keystore Password**: `brainops`
- **Key Alias**: `brainops`
- **Key Password**: `brainops`
- **Keystore Type**: PKCS12

## Certificate Fingerprint

SHA-256: `0F:6D:16:92:78:25:B3:3F:3C:FD:1E:DB:99:FD:F7:21:1D:00:01:24:05:56:15:E8:E0:AE:18:1F:74:A4:18:FF`

## Signing Commands

### Build Release APK
```bash
./gradlew assembleBasicRelease --no-daemon -Dorg.gradle.jvmargs="-Xmx4g"
```

### Sign APK
```bash
apksigner sign \
  --ks brainops-release.jks \
  --ks-pass pass:brainops \
  --key-pass pass:brainops \
  --out app-basic-universal-release-signed-v${VERSION}.apk \
  app/build/outputs/apk/basic/release/app-basic-universal-release-unsigned.apk
```

### Verify Signature
```bash
apksigner verify app-basic-universal-release-signed-v${VERSION}.apk
```

## Output APK Locations

- **Unsigned**: `app/build/outputs/apk/basic/release/app-basic-universal-release-unsigned.apk`
- **Signed**: Root directory as `app-basic-universal-release-signed-v${VERSION}.apk`

## Notes

- Always bump version in `app/build.gradle.kts` before building
- The keystore was created on Nov 24, 2025
- Warnings about META-INF files not being protected are normal for Kotlin/Gradle projects
