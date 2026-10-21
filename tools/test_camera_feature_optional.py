#!/usr/bin/env python3
"""Guarda o Android Lint PermissionImpliesUnsupportedChromeOsHardware do QR Code."""
from pathlib import Path
import xml.etree.ElementTree as ET

root=Path(__file__).resolve().parents[1]
manifest=root/'app/src/main/AndroidManifest.xml'
ns='{http://schemas.android.com/apk/res/android}'

def valid(source: str) -> bool:
    element=ET.fromstring(source)
    permissions={node.get(ns+'name') for node in element.findall('uses-permission')}
    features={node.get(ns+'name'):node.get(ns+'required') for node in element.findall('uses-feature')}
    return ('android.permission.CAMERA' in permissions
            and features.get('android.hardware.camera') == 'false'
            and features.get('android.hardware.camera.autofocus') == 'false')

actual=manifest.read_text()
assert valid(actual), 'Camera necessaria para QR, mas hardware deve ser opcional'
assert not valid(actual.replace('android:required="false"', 'android:required="true"', 1)), 'Feature obrigatoria deve falhar'
assert not valid(actual.replace('<uses-feature android:name="android.hardware.camera" android:required="false"/>','')), 'Feature ausente deve falhar'
assert not valid(actual.replace('<uses-permission android:name="android.permission.CAMERA"/>','')), 'Permissao ausente deve falhar'
print('PASS: CAMERA concedida para QR, camera/autofoco opcionais e 3 cenarios negativos')
