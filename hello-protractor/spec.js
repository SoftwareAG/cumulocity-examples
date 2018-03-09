// spec.js
describe('Cumulocity demo test project', function () {

  // ADJUST THIS
  const username = 'username';
  const password = 'password';
  const tenant = 'tenant';

  const baseURL = 'https://tenant.installation.tld';

  const EC = protractor.ExpectedConditions;


  const userInput = '$(\'[ng-model="ctrl.user.name"]\')',
    logoutBtn = element(by.css('[ng-click*="logout()"]')),
    navigator = element(by.css('.navigator')),
    userDropDown = element(by.binding('getUserName()')),
    pageTitle = element(by.css('c8y-ui-title'));

  const waitForAngular = (elem) => {
    browser.wait(() => browser.executeScript(`return ${elem}.length`), 20000);
  };

  const closeAlerts = () => {
    browser.executeScript('$(\'.alerts\')[0].className += \' hidden\';');
  };

  const login = (tenant, user, pass) => {
    waitForAngular(userInput);
    const cred = `${tenant}/${user}`;
    browser.executeScript(`${userInput}.val(arguments[0]).trigger(\'input\');`, cred);
    browser.executeScript('$(\'[ng-model="ctrl.user.password"]\').val(arguments[0]).trigger(\'input\');', pass);
    browser.executeScript('$(\'[ng-model="ctrl.user.rememberMe"]\').click();');
    browser.executeScript('$(\'[name="form_login"] [type="submit"]\').click();');
    browser.sleep(500); // needed if error message appears
    waitForAngular('$(\'.navigator\')');
  };

  const logout = () => {
    closeAlerts();
    browser.wait(EC.elementToBeClickable(userDropDown), 10000);
    userDropDown.click();
    browser.wait(EC.elementToBeClickable(logoutBtn));
    logoutBtn.click();
    waitForAngular(userInput);
  };

  const clickNavigationBarLink = (itemName) => {
    navigator.element(by.css(`[title="${itemName}"]`)).click();
    browser.sleep(500);
  };

  beforeEach(() => {
    browser.driver.manage().window().maximize();
    browser.get(baseURL);
    login(tenant, username, password);
  });

  afterEach(() => {
    logout();
  });

  it('should log in successfully', () => {
    expect(browser.getTitle()).toContain('Cockpit');
  });

  it('should show alarm list', () => {
    clickNavigationBarLink('Alarms');
    expect(pageTitle.getText()).toContain('Alarms');
  });
})
;
