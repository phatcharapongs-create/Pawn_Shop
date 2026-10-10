/* Pawnshop UI — พฤติกรรมเล็ก ๆ ที่ใช้ร่วมทุกหน้า */
(function () {
  'use strict';

  var nav = document.getElementById('main-nav');
  var toggle = document.querySelector('.nav-toggle');

  // เปิด/ปิดเมนูบนมือถือ
  if (nav && toggle) {
    toggle.addEventListener('click', function () {
      var open = nav.classList.toggle('open');
      toggle.setAttribute('aria-expanded', String(open));
    });
  }

  // ไฮไลต์เมนูที่ตรงกับหน้าปัจจุบัน (เลือกลิงก์ที่ path ยาวที่สุดและเป็น prefix ของหน้านี้)
  if (nav) {
    var clean = function (p) { return p.replace(/\/+$/, '') || '/'; };
    var current = clean(window.location.pathname);
    var best = null, bestLen = -1;
    nav.querySelectorAll('a[href]').forEach(function (a) {
      var p = clean(new URL(a.href, window.location.origin).pathname);
      var match = current === p || current.indexOf(p + '/') === 0;
      if (match && p.length > bestLen) { best = a; bestLen = p.length; }
    });
    if (best) {
      best.classList.add('active');
      best.setAttribute('aria-current', 'page');
    }
  }

  // Flash alerts แสดงเป็น toast ด้านบนและปิดเอง (กล่องคำแนะนำ role=note คงอยู่ในหน้า)
  document.querySelectorAll('[data-dismiss="alert"]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var box = btn.closest('.alert');
      if (box) dismissToast(box);
    });
  });
  var dismissToast = function (box) {
    if (!box || box.classList.contains('toast-closing')) return;
    box.classList.add('toast-closing');
    window.setTimeout(function () { box.remove(); }, 240);
  };
  document.querySelectorAll('.alert:not([role="note"])').forEach(function (alert) {
    alert.classList.add('alert-toast');
    var delay = alert.classList.contains('alert-error') ? 7000 : 5000;
    var timer = window.setTimeout(function () { dismissToast(alert); }, delay);
    alert.addEventListener('mouseenter', function () { window.clearTimeout(timer); });
    alert.addEventListener('mouseleave', function () {
      timer = window.setTimeout(function () { dismissToast(alert); }, 1800);
    });
  });

  // แสดงช่องสร้างบัญชีทันทีเมื่อเลือก checkbox (สคริปต์ใน fragment อาจไม่ถูกนำไปแสดง)
  var accountToggle = document.getElementById('createAccount');
  var accountFields = document.getElementById('accountSetupFields');
  if (accountToggle && accountFields) {
    var username = document.getElementById('username');
    var initialPassword = document.getElementById('initialPassword');
    var syncAccountFields = function () {
      var enabled = accountToggle.checked;
      accountFields.classList.toggle('account-setup-open', enabled);
      [username, initialPassword].forEach(function (input) {
        if (!input) return;
        input.required = enabled;
        input.disabled = !enabled;
      });
    };
    accountToggle.addEventListener('change', syncAccountFields);
    syncAccountFields();
  }

  // กล่องยืนยันรายการในธีมเดียวกับระบบ
  document.querySelectorAll('[data-dialog-target]').forEach(function (trigger) {
    trigger.addEventListener('click', function () {
      var dialog = document.getElementById(trigger.getAttribute('data-dialog-target'));
      if (dialog && typeof dialog.showModal === 'function') dialog.showModal();
    });
  });
  document.querySelectorAll('[data-dialog-close]').forEach(function (button) {
    button.addEventListener('click', function () {
      var dialog = button.closest('dialog');
      if (dialog) dialog.close();
    });
  });
  document.querySelectorAll('[data-dialog-submit]').forEach(function (button) {
    button.addEventListener('click', function () {
      var form = document.getElementById(button.getAttribute('data-dialog-submit'));
      if (form && typeof form.requestSubmit === 'function') form.requestSubmit();
      else if (form) form.submit();
    });
  });

  // กล่องยืนยันกลางสำหรับฟอร์มสำคัญ แทน browser confirm ที่หน้าตาไม่เข้ากับระบบ
  var confirmDialog = document.createElement('dialog');
  confirmDialog.className = 'confirm-dialog';
  confirmDialog.setAttribute('aria-labelledby', 'globalConfirmTitle');
  confirmDialog.setAttribute('aria-describedby', 'globalConfirmMessage');
  confirmDialog.innerHTML = '<div class="confirm-dialog-mark" aria-hidden="true">!</div>' +
    '<p class="confirm-dialog-eyebrow">ยืนยันรายการ</p>' +
    '<h2 id="globalConfirmTitle">ยืนยันการทำรายการ</h2>' +
    '<p id="globalConfirmMessage" class="confirm-dialog-copy"></p>' +
    '<div class="confirm-dialog-note" hidden><span class="confirm-dialog-note-icon" aria-hidden="true">✓</span><span></span></div>' +
    '<div class="confirm-dialog-actions"><button type="button" class="btn btn-outline" data-global-confirm-cancel>ยกเลิก</button>' +
    '<button type="button" class="btn btn-primary" data-global-confirm-accept>ยืนยัน</button></div>';
  document.body.appendChild(confirmDialog);
  var pendingForm = null;
  document.addEventListener('submit', function (event) {
    var form = event.target;
    if (!form.matches('form[data-confirm-message]')) return;
    if (form.dataset.confirmed === 'true') {
      delete form.dataset.confirmed;
      return;
    }
    event.preventDefault();
    pendingForm = form;
    confirmDialog.querySelector('#globalConfirmTitle').textContent = form.dataset.confirmTitle || 'ยืนยันการทำรายการ';
    confirmDialog.querySelector('#globalConfirmMessage').textContent = form.dataset.confirmMessage;
    confirmDialog.querySelector('.confirm-dialog-eyebrow').textContent = form.dataset.confirmEyebrow || 'ยืนยันรายการ';
    confirmDialog.querySelector('.confirm-dialog-mark').textContent = form.dataset.confirmIcon || '!';
    confirmDialog.querySelector('[data-global-confirm-accept]').textContent = form.dataset.confirmAccept || 'ยืนยัน';
    confirmDialog.classList.toggle('confirm-maintenance', form.dataset.confirmTheme === 'maintenance');
    var note = confirmDialog.querySelector('.confirm-dialog-note');
    note.hidden = !form.dataset.confirmNote;
    note.lastElementChild.textContent = form.dataset.confirmNote || '';
    confirmDialog.showModal();
  });
  confirmDialog.querySelector('[data-global-confirm-cancel]').addEventListener('click', function () {
    confirmDialog.close();
    pendingForm = null;
  });
  confirmDialog.querySelector('[data-global-confirm-accept]').addEventListener('click', function () {
    if (!pendingForm) return;
    var form = pendingForm;
    pendingForm = null;
    form.dataset.confirmed = 'true';
    confirmDialog.close();
    form.requestSubmit();
  });
})();
