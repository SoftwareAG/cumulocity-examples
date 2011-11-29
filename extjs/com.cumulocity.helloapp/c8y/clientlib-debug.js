

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>
  m2m / cumulocity-platform-ui / source &mdash; Bitbucket
</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="description" content="" />
  <meta name="keywords" content="" />
  <!--[if lt IE 9]>
  <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/js/lib/html5.js"></script>
  <![endif]-->

  <script>
    (function (window) {
      // prevent stray occurrences of `console.log` from causing errors in IE
      var console = window.console || (window.console = {});
      console.log || (console.log = function () {});

      var BB = window.BB || (window.BB = {});
      BB.debug = false;
      BB.cname = false;
      BB.CANON_URL = 'https://bitbucket.org';
      BB.MEDIA_URL = 'https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/';
      BB.images = {
        noAvatar: 'https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/img/no_avatar.png'
      };
      BB.user || (BB.user = {});
      BB.user.has = (function () {
        var betaFeatures = [];
        betaFeatures.push('repo2');
        return function (feature) {
          return _.contains(betaFeatures, feature);
        };
      }());
      BB.repo || (BB.repo = {});
  
      BB.user.follows = {
        repos: ''.split(',')
      };
      BB.user.id = 196483;
    
      BB.user.username = 'eickler';
    
      BB.user.isSshEnabled = true;
  
  
      BB.user.isAdmin = false;
      BB.repo.id = 378311;
    
    
      BB.repo.language = 'javascript';
      BB.repo.pygmentsLanguage = 'javascript';
    
    
      BB.repo.slug = 'cumulocity\u002Dplatform\u002Dui';
    
    
      BB.repo.owner = {
        username: 'm2m'
      };
    
      // Coerce `BB.repo` to a string to get
      // "davidchambers/mango" or whatever.
      BB.repo.toString = function () {
        return BB.cname ? this.slug : this.owner.username + '/' + this.slug;
      }
    
      BB.changeset = 'cee073ad5c39'
    
    
  
    }(this));
  </script>

  


  <link rel="stylesheet" href="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/bun/css/bundle.css"/>



  <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="Bitbucket" />
  <link rel="icon" href="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/img/logo_new.png" type="image/png" />
  <link type="text/plain" rel="author" href="/humans.txt" />


  
    <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/bun/js/bundle.js"></script>
  



</head>

<body id="" class="">
  <script type="text/javascript">
    if (!RegExp(" AppleWebKit/").test(navigator.userAgent)) {
    $('body').addClass('non-webkit');
    }
  </script>
  <!--[if IE 8]>
  <script>jQuery(document.body).addClass('ie8')</script>
  <![endif]-->
  <!--[if IE 9]>
  <script>jQuery(document.body).addClass('ie9')</script>
  <![endif]-->

  <div id="wrapper">



  <div id="header-wrap">
    <div id="header">
    <ul id="global-nav">
      <li><a class="home" href="http://www.atlassian.com">Atlassian Home</a></li>
      <li><a class="docs" href="http://confluence.atlassian.com/display/BITBUCKET">Documentation</a></li>
      <li><a class="support" href="/support">Support</a></li>
      <li><a class="blog" href="http://blog.bitbucket.org">Blog</a></li>
      <li><a class="forums" href="http://groups.google.com/group/bitbucket-users">Forums</a></li>
    </ul>
    <a href="/" id="logo">Bitbucket by Atlassian</a>

    <div id="main-nav">
    

      <ul class="clearfix">
        <li><a href="/explore" id="explore-link">Explore</a></li>
        <li><a href="https://bitbucket.org" id="dashboard-link">Dashboard</a></li>
        <li id="repositories-dropdown" class="inertial-hover active">
          <a class="drop-arrow" href="/repo/mine" id="repositories-link">Repositories</a>
          <div>
            <div>
              <div id="repo-overview"></div>
              <div class="group">
                <a href="/repo/create" class="new-repository" id="create-repo-link">Create repository</a>
                <a href="/repo/import" class="import-repository" id="import-repo-link">Import repository</a>
              </div>
            </div>
          </div>
        </li>
        <li id="user-dropdown" class="inertial-hover">
          <a class="drop-arrow" href="/eickler">
            <span>André Eickler</span>
          </a>
          <div>
            <div>
              <div class="group">
                <a href="/account/" id="account-link">Account</a>
                <a href="/account/notifications/" id="inbox-link">Inbox <span>(6)</span></a>
              </div>
              <div class="group">
                <a href="/account/signout/">Log out</a>
              </div>
            </div>
          </div>
        </li>
        

<li class="search-box">
  
    <form action="/repo/all">
      <input type="search" results="5" autosave="bitbucket-explore-search"
             name="name" id="searchbox"
             placeholder="Find a project" />
  
  </form>
</li>

      </ul>

    
    </div>
    </div>
  </div>

    <div id="header-messages">
  
  
    
    
    
    
  

    
   </div>



    <div id="content">
      <div id="source">
      
  
  





  <script>
    jQuery(function ($) {
        var cookie = $.cookie,
            cookieOptions, date,
            $content = $('#content'),
            $pane = $('#what-is-bitbucket'),
            $hide = $pane.find('[href="#hide"]').css('display', 'block').hide();

        date = new Date();
        date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000);
        cookieOptions = { path: '/', expires: date };

        if (cookie('toggle_status') == 'hide') $content.addClass('repo-desc-hidden');

        $('#toggle-repo-content').click(function (event) {
            event.preventDefault();
            $content.toggleClass('repo-desc-hidden');
            cookie('toggle_status', cookie('toggle_status') == 'show' ? 'hide' : 'show', cookieOptions);
        });

        if (!cookie('hide_intro_message')) $pane.show();

        $hide.click(function (event) {
            event.preventDefault();
            cookie('hide_intro_message', true, cookieOptions);
            $pane.slideUp('slow');
        });

        $pane.hover(
            function () { $hide.fadeIn('fast'); },
            function () { $hide.fadeOut('fast'); });

      (function () {
        // Update "recently-viewed-repos" cookie for
        // the "repositories" drop-down.
        var
          id = BB.repo.id,
          cookieName = 'recently-viewed-repos_' + BB.user.id,
          rvr = cookie(cookieName),
          ids = rvr? rvr.split(','): [],
          idx = _.indexOf(ids, '' + id);

        // Remove `id` from `ids` if present.
        if (~idx) ids.splice(idx, 1);

        cookie(
          cookieName,
          // Insert `id` as the first item, then call
          // `join` on the resulting array to produce
          // something like "114694,27542,89002,84570".
          [id].concat(ids.slice(0, 4)).join(),
          {path: '/', expires: 1e6} // "never" expires
        );
      }());
    });
  </script>




<div id="tabs">
  <ul class="tabs">
    <li>
      <a href="/m2m/cumulocity-platform-ui/overview" id="repo-overview-link">Overview</a>
    </li>

    <li>
      <a href="/m2m/cumulocity-platform-ui/downloads" id="repo-downloads-link">Downloads (<span id="downloads-count">0</span>)</a>
    </li>

    

    <li>
      <a href="/m2m/cumulocity-platform-ui/pull-requests" id="repo-pr-link">Pull requests (0)</a>
    </li>

    <li class="selected">
      
        <a href="/m2m/cumulocity-platform-ui/src" id="repo-source-link">Source</a>
      
    </li>

    <li>
      <a href="/m2m/cumulocity-platform-ui/changesets" id="repo-commits-link">Commits</a>
    </li>

    <li id="wiki-tab" class="dropdown"
      style="display:
                        none  
        
      ">
      <a href="/m2m/cumulocity-platform-ui/wiki" id="repo-wiki-link">Wiki</a>
    </li>

    <li id="issues-tab" class="dropdown inertial-hover"
      style="display:
                      none  
        
      ">
      <a href="/m2m/cumulocity-platform-ui/issues?status=new&amp;status=open" id="repo-issues-link">Issues (0) &raquo;</a>
      <ul>
        <li><a href="/m2m/cumulocity-platform-ui/issues/new">Create new issue</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=new">New issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=new&amp;status=open">Open issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=duplicate&amp;status=invalid&amp;status=resolved&amp;status=wontfix">Closed issues</a></li>
        
          <li><a href="/m2m/cumulocity-platform-ui/issues?responsible=eickler">My issues</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/issues">All issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues/query">Advanced query</a></li>
      </ul>
    </li>

    

    <li class="secondary">
      <a href="/m2m/cumulocity-platform-ui/descendants">Forks/queues (0)</a>
    </li>

    <li class="secondary">
      <a href="/m2m/cumulocity-platform-ui/zealots">Followers (<span id="followers-count">3</span>)</a>
    </li>
  </ul>
</div>



 

  <div class="repo-menu" id="repo-menu">
    <ul id="repo-menu-links">
    
      <li>
        <a href="/m2m/cumulocity-platform-ui/rss?token=2d4d5110c13c4c60e3e28e4ab7cd1471" class="rss" title="RSS feed for cumulocity-platform-ui">RSS</a>
      </li>

      <li><a href="/m2m/cumulocity-platform-ui/fork" class="fork">fork</a></li>
      
        
          <li><a href="/m2m/cumulocity-platform-ui/hack" class="patch-queue">patch queue</a></li>
        
      
      <li>
        <a rel="nofollow" href="/m2m/cumulocity-platform-ui/follow" class="follow">follow</a>
      </li>
      
          
            <li>
              <a href="/account/notifications/send/?receiver=m2m&amp;subject=revoke%20my%20write%20access%20to%20m2m/cumulocity-platform-ui&amp;message=Currently%20I%20have%20access%20to%20the%20m2m/cumulocity-platform-ui%20via%20the%20group%20m2m%3Adevelopers.%0A%0ACould%20you%20please%20revoke%20my%20write%20access%20to%20the%20repository%20m2m/cumulocity-platform-ui%3F" class="revoke-group" title="Your access to this repository is via a group. To remove your access, you will need to contact the group owner.">revoke</a>
            </li>
          
      
      
        <li class="get-source inertial-hover">
          <a class="source">get source</a>
          <ul class="downloads">
            
              
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.zip">zip</a></li>
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.tar.gz">gz</a></li>
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.tar.bz2">bz2</a></li>
            
          </ul>
        </li>
      
    </ul>

  
    <ul class="metadata">
      
      
        <li class="branches inertial-hover">branches
          <ul>
            <li><a href="/m2m/cumulocity-platform-ui/src/aeeb1099bfb6">build</a>
              
              
              <a rel="nofollow" class="menu-compare"
                 href="/m2m/cumulocity-platform-ui/compare/build..default"
                 title="Show changes between build and the main branch.">compare</a>
              
            </li>
            <li><a href="/m2m/cumulocity-platform-ui/src/e010153b0268">default</a>
              
              
            </li>
          </ul>
        </li>
      
      
      <li class="tags inertial-hover">tags
        <ul>
          <li><a href="/m2m/cumulocity-platform-ui/src/e010153b0268">tip</a>
            
            </li>
          <li><a href="/m2m/cumulocity-platform-ui/src/4745070bec53">ui-0.5.0</a>
            
            
              <a rel="nofollow" class='menu-compare'
                 href="/m2m/cumulocity-platform-ui/compare/..ui-0.5.0"
                 title="Show changes between ui-0.5.0 and the main branch.">compare</a>
            </li>
        </ul>
      </li>
     
     
    </ul>
  
</div>

<div class="repo-menu" id="repo-desc">
  
    <ul id="repo-menu-links-mini">
      <li><a rel="nofollow" class="compare-link"
             href="/m2m/cumulocity-platform-ui/compare/../"
             title="Show changes between cumulocity-platform-ui and "
             ></a></li>
      
  

      
      <li>
        <a href="/m2m/cumulocity-platform-ui/rss?token=2d4d5110c13c4c60e3e28e4ab7cd1471" class="rss" title="RSS feed for cumulocity-platform-ui"></a>
      </li>

      <li><a href="/m2m/cumulocity-platform-ui/fork" class="fork" title="Fork"></a></li>
      
        
          <li><a href="/m2m/cumulocity-platform-ui/hack" class="patch-queue" title="Patch queue"></a></li>
        
      
      <li>
        <a rel="nofollow" href="/m2m/cumulocity-platform-ui/follow" class="follow">follow</a>
      </li>
      
          
            <li>
              <a href="/account/notifications/send/?receiver=m2m&amp;subject=revoke%20my%20write%20access%20to%20m2m/cumulocity-platform-ui&amp;message=Currently%20I%20have%20access%20to%20the%20m2m/cumulocity-platform-ui%20via%20the%20group%20m2m%3Adevelopers.%0A%0ACould%20you%20please%20revoke%20my%20write%20access%20to%20the%20repository%20m2m/cumulocity-platform-ui%3F" class="revoke-group" title="Your access to this repository is via a group. To remove your access, you will need to contact the group owner.">revoke</a>
            </li>
          
      
    
      
        <li>
          <a class="source" title="Get source"></a>
          <ul class="downloads">
            
              
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.zip">zip</a></li>
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.tar.gz">gz</a></li>
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.tar.bz2">bz2</a></li>
              
            
          </ul>
        </li>
      
    
    </ul>

    <h3 id="repo-heading" class="private hg">
      <a class="owner-username" href="/m2m">m2m</a> /
      <a class="repo-name" href="/m2m/cumulocity-platform-ui">cumulocity-platform-ui</a>
    

    
    </h3>

        

  <div id="repo-desc-cloneinfo">Clone this repository (size: 86.4 MB):
    <a href="https://eickler@bitbucket.org/m2m/cumulocity-platform-ui" class="https">HTTPS</a> /
    <a href="ssh://hg@bitbucket.org/m2m/cumulocity-platform-ui" class="ssh">SSH</a>
    <pre id="clone-url-https">hg clone https://eickler@bitbucket.org/m2m/cumulocity-platform-ui</pre>
    <pre id="clone-url-ssh">hg clone ssh://hg@bitbucket.org/m2m/cumulocity-platform-ui</pre>
    
  </div>

        <a href="#" id="toggle-repo-content"></a>

        

</div>




      
  <div id="source-container">
    

  <div id="source-path">
    <h1>
      <a href="/m2m/cumulocity-platform-ui/src" class="src-pjax">cumulocity-platform-ui</a> /

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/" class="src-pjax">build</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/" class="src-pjax">src</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/" class="src-pjax">main</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/webapp/" class="src-pjax">webapp</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/webapp/c8y/" class="src-pjax">c8y</a> /
    
  

  
    
      clientlib-debug.js
    
  

    </h1>
  </div>

  <div class="labels labels-csv">
  
    <dl>
  
    
  
  
    
  
  
</dl>

  
  </div>


  
  <div id="source-view">
    <div class="header">
      <ul class="metadata">
        <li><code>cee073ad5c39</code></li>
        
          
            <li>1631 loc</li>
          
        
        <li>44.0 KB</li>
      </ul>
      <ul class="source-view-links">
        
        <li><a href="/m2m/cumulocity-platform-ui/history/build/src/main/webapp/c8y/clientlib-debug.js">history</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/annotate/cee073ad5c39/build/src/main/webapp/c8y/clientlib-debug.js">annotate</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/raw/cee073ad5c39/build/src/main/webapp/c8y/clientlib-debug.js">raw</a></li>
        <li>
          <form action="/m2m/cumulocity-platform-ui/diff/build/src/main/webapp/c8y/clientlib-debug.js" class="source-view-form">
          
            <input type="hidden" name="diff2" value="cee073ad5c39" />
            <select name="diff1">
            
              
            
            </select>
            <input type="submit" value="diff" />
          
          </form>
        </li>
      </ul>
    </div>
  
    <div>
    <table class="highlighttable"><tr><td class="linenos"><div class="linenodiv"><pre><a href="#cl-1">   1</a>
<a href="#cl-2">   2</a>
<a href="#cl-3">   3</a>
<a href="#cl-4">   4</a>
<a href="#cl-5">   5</a>
<a href="#cl-6">   6</a>
<a href="#cl-7">   7</a>
<a href="#cl-8">   8</a>
<a href="#cl-9">   9</a>
<a href="#cl-10">  10</a>
<a href="#cl-11">  11</a>
<a href="#cl-12">  12</a>
<a href="#cl-13">  13</a>
<a href="#cl-14">  14</a>
<a href="#cl-15">  15</a>
<a href="#cl-16">  16</a>
<a href="#cl-17">  17</a>
<a href="#cl-18">  18</a>
<a href="#cl-19">  19</a>
<a href="#cl-20">  20</a>
<a href="#cl-21">  21</a>
<a href="#cl-22">  22</a>
<a href="#cl-23">  23</a>
<a href="#cl-24">  24</a>
<a href="#cl-25">  25</a>
<a href="#cl-26">  26</a>
<a href="#cl-27">  27</a>
<a href="#cl-28">  28</a>
<a href="#cl-29">  29</a>
<a href="#cl-30">  30</a>
<a href="#cl-31">  31</a>
<a href="#cl-32">  32</a>
<a href="#cl-33">  33</a>
<a href="#cl-34">  34</a>
<a href="#cl-35">  35</a>
<a href="#cl-36">  36</a>
<a href="#cl-37">  37</a>
<a href="#cl-38">  38</a>
<a href="#cl-39">  39</a>
<a href="#cl-40">  40</a>
<a href="#cl-41">  41</a>
<a href="#cl-42">  42</a>
<a href="#cl-43">  43</a>
<a href="#cl-44">  44</a>
<a href="#cl-45">  45</a>
<a href="#cl-46">  46</a>
<a href="#cl-47">  47</a>
<a href="#cl-48">  48</a>
<a href="#cl-49">  49</a>
<a href="#cl-50">  50</a>
<a href="#cl-51">  51</a>
<a href="#cl-52">  52</a>
<a href="#cl-53">  53</a>
<a href="#cl-54">  54</a>
<a href="#cl-55">  55</a>
<a href="#cl-56">  56</a>
<a href="#cl-57">  57</a>
<a href="#cl-58">  58</a>
<a href="#cl-59">  59</a>
<a href="#cl-60">  60</a>
<a href="#cl-61">  61</a>
<a href="#cl-62">  62</a>
<a href="#cl-63">  63</a>
<a href="#cl-64">  64</a>
<a href="#cl-65">  65</a>
<a href="#cl-66">  66</a>
<a href="#cl-67">  67</a>
<a href="#cl-68">  68</a>
<a href="#cl-69">  69</a>
<a href="#cl-70">  70</a>
<a href="#cl-71">  71</a>
<a href="#cl-72">  72</a>
<a href="#cl-73">  73</a>
<a href="#cl-74">  74</a>
<a href="#cl-75">  75</a>
<a href="#cl-76">  76</a>
<a href="#cl-77">  77</a>
<a href="#cl-78">  78</a>
<a href="#cl-79">  79</a>
<a href="#cl-80">  80</a>
<a href="#cl-81">  81</a>
<a href="#cl-82">  82</a>
<a href="#cl-83">  83</a>
<a href="#cl-84">  84</a>
<a href="#cl-85">  85</a>
<a href="#cl-86">  86</a>
<a href="#cl-87">  87</a>
<a href="#cl-88">  88</a>
<a href="#cl-89">  89</a>
<a href="#cl-90">  90</a>
<a href="#cl-91">  91</a>
<a href="#cl-92">  92</a>
<a href="#cl-93">  93</a>
<a href="#cl-94">  94</a>
<a href="#cl-95">  95</a>
<a href="#cl-96">  96</a>
<a href="#cl-97">  97</a>
<a href="#cl-98">  98</a>
<a href="#cl-99">  99</a>
<a href="#cl-100"> 100</a>
<a href="#cl-101"> 101</a>
<a href="#cl-102"> 102</a>
<a href="#cl-103"> 103</a>
<a href="#cl-104"> 104</a>
<a href="#cl-105"> 105</a>
<a href="#cl-106"> 106</a>
<a href="#cl-107"> 107</a>
<a href="#cl-108"> 108</a>
<a href="#cl-109"> 109</a>
<a href="#cl-110"> 110</a>
<a href="#cl-111"> 111</a>
<a href="#cl-112"> 112</a>
<a href="#cl-113"> 113</a>
<a href="#cl-114"> 114</a>
<a href="#cl-115"> 115</a>
<a href="#cl-116"> 116</a>
<a href="#cl-117"> 117</a>
<a href="#cl-118"> 118</a>
<a href="#cl-119"> 119</a>
<a href="#cl-120"> 120</a>
<a href="#cl-121"> 121</a>
<a href="#cl-122"> 122</a>
<a href="#cl-123"> 123</a>
<a href="#cl-124"> 124</a>
<a href="#cl-125"> 125</a>
<a href="#cl-126"> 126</a>
<a href="#cl-127"> 127</a>
<a href="#cl-128"> 128</a>
<a href="#cl-129"> 129</a>
<a href="#cl-130"> 130</a>
<a href="#cl-131"> 131</a>
<a href="#cl-132"> 132</a>
<a href="#cl-133"> 133</a>
<a href="#cl-134"> 134</a>
<a href="#cl-135"> 135</a>
<a href="#cl-136"> 136</a>
<a href="#cl-137"> 137</a>
<a href="#cl-138"> 138</a>
<a href="#cl-139"> 139</a>
<a href="#cl-140"> 140</a>
<a href="#cl-141"> 141</a>
<a href="#cl-142"> 142</a>
<a href="#cl-143"> 143</a>
<a href="#cl-144"> 144</a>
<a href="#cl-145"> 145</a>
<a href="#cl-146"> 146</a>
<a href="#cl-147"> 147</a>
<a href="#cl-148"> 148</a>
<a href="#cl-149"> 149</a>
<a href="#cl-150"> 150</a>
<a href="#cl-151"> 151</a>
<a href="#cl-152"> 152</a>
<a href="#cl-153"> 153</a>
<a href="#cl-154"> 154</a>
<a href="#cl-155"> 155</a>
<a href="#cl-156"> 156</a>
<a href="#cl-157"> 157</a>
<a href="#cl-158"> 158</a>
<a href="#cl-159"> 159</a>
<a href="#cl-160"> 160</a>
<a href="#cl-161"> 161</a>
<a href="#cl-162"> 162</a>
<a href="#cl-163"> 163</a>
<a href="#cl-164"> 164</a>
<a href="#cl-165"> 165</a>
<a href="#cl-166"> 166</a>
<a href="#cl-167"> 167</a>
<a href="#cl-168"> 168</a>
<a href="#cl-169"> 169</a>
<a href="#cl-170"> 170</a>
<a href="#cl-171"> 171</a>
<a href="#cl-172"> 172</a>
<a href="#cl-173"> 173</a>
<a href="#cl-174"> 174</a>
<a href="#cl-175"> 175</a>
<a href="#cl-176"> 176</a>
<a href="#cl-177"> 177</a>
<a href="#cl-178"> 178</a>
<a href="#cl-179"> 179</a>
<a href="#cl-180"> 180</a>
<a href="#cl-181"> 181</a>
<a href="#cl-182"> 182</a>
<a href="#cl-183"> 183</a>
<a href="#cl-184"> 184</a>
<a href="#cl-185"> 185</a>
<a href="#cl-186"> 186</a>
<a href="#cl-187"> 187</a>
<a href="#cl-188"> 188</a>
<a href="#cl-189"> 189</a>
<a href="#cl-190"> 190</a>
<a href="#cl-191"> 191</a>
<a href="#cl-192"> 192</a>
<a href="#cl-193"> 193</a>
<a href="#cl-194"> 194</a>
<a href="#cl-195"> 195</a>
<a href="#cl-196"> 196</a>
<a href="#cl-197"> 197</a>
<a href="#cl-198"> 198</a>
<a href="#cl-199"> 199</a>
<a href="#cl-200"> 200</a>
<a href="#cl-201"> 201</a>
<a href="#cl-202"> 202</a>
<a href="#cl-203"> 203</a>
<a href="#cl-204"> 204</a>
<a href="#cl-205"> 205</a>
<a href="#cl-206"> 206</a>
<a href="#cl-207"> 207</a>
<a href="#cl-208"> 208</a>
<a href="#cl-209"> 209</a>
<a href="#cl-210"> 210</a>
<a href="#cl-211"> 211</a>
<a href="#cl-212"> 212</a>
<a href="#cl-213"> 213</a>
<a href="#cl-214"> 214</a>
<a href="#cl-215"> 215</a>
<a href="#cl-216"> 216</a>
<a href="#cl-217"> 217</a>
<a href="#cl-218"> 218</a>
<a href="#cl-219"> 219</a>
<a href="#cl-220"> 220</a>
<a href="#cl-221"> 221</a>
<a href="#cl-222"> 222</a>
<a href="#cl-223"> 223</a>
<a href="#cl-224"> 224</a>
<a href="#cl-225"> 225</a>
<a href="#cl-226"> 226</a>
<a href="#cl-227"> 227</a>
<a href="#cl-228"> 228</a>
<a href="#cl-229"> 229</a>
<a href="#cl-230"> 230</a>
<a href="#cl-231"> 231</a>
<a href="#cl-232"> 232</a>
<a href="#cl-233"> 233</a>
<a href="#cl-234"> 234</a>
<a href="#cl-235"> 235</a>
<a href="#cl-236"> 236</a>
<a href="#cl-237"> 237</a>
<a href="#cl-238"> 238</a>
<a href="#cl-239"> 239</a>
<a href="#cl-240"> 240</a>
<a href="#cl-241"> 241</a>
<a href="#cl-242"> 242</a>
<a href="#cl-243"> 243</a>
<a href="#cl-244"> 244</a>
<a href="#cl-245"> 245</a>
<a href="#cl-246"> 246</a>
<a href="#cl-247"> 247</a>
<a href="#cl-248"> 248</a>
<a href="#cl-249"> 249</a>
<a href="#cl-250"> 250</a>
<a href="#cl-251"> 251</a>
<a href="#cl-252"> 252</a>
<a href="#cl-253"> 253</a>
<a href="#cl-254"> 254</a>
<a href="#cl-255"> 255</a>
<a href="#cl-256"> 256</a>
<a href="#cl-257"> 257</a>
<a href="#cl-258"> 258</a>
<a href="#cl-259"> 259</a>
<a href="#cl-260"> 260</a>
<a href="#cl-261"> 261</a>
<a href="#cl-262"> 262</a>
<a href="#cl-263"> 263</a>
<a href="#cl-264"> 264</a>
<a href="#cl-265"> 265</a>
<a href="#cl-266"> 266</a>
<a href="#cl-267"> 267</a>
<a href="#cl-268"> 268</a>
<a href="#cl-269"> 269</a>
<a href="#cl-270"> 270</a>
<a href="#cl-271"> 271</a>
<a href="#cl-272"> 272</a>
<a href="#cl-273"> 273</a>
<a href="#cl-274"> 274</a>
<a href="#cl-275"> 275</a>
<a href="#cl-276"> 276</a>
<a href="#cl-277"> 277</a>
<a href="#cl-278"> 278</a>
<a href="#cl-279"> 279</a>
<a href="#cl-280"> 280</a>
<a href="#cl-281"> 281</a>
<a href="#cl-282"> 282</a>
<a href="#cl-283"> 283</a>
<a href="#cl-284"> 284</a>
<a href="#cl-285"> 285</a>
<a href="#cl-286"> 286</a>
<a href="#cl-287"> 287</a>
<a href="#cl-288"> 288</a>
<a href="#cl-289"> 289</a>
<a href="#cl-290"> 290</a>
<a href="#cl-291"> 291</a>
<a href="#cl-292"> 292</a>
<a href="#cl-293"> 293</a>
<a href="#cl-294"> 294</a>
<a href="#cl-295"> 295</a>
<a href="#cl-296"> 296</a>
<a href="#cl-297"> 297</a>
<a href="#cl-298"> 298</a>
<a href="#cl-299"> 299</a>
<a href="#cl-300"> 300</a>
<a href="#cl-301"> 301</a>
<a href="#cl-302"> 302</a>
<a href="#cl-303"> 303</a>
<a href="#cl-304"> 304</a>
<a href="#cl-305"> 305</a>
<a href="#cl-306"> 306</a>
<a href="#cl-307"> 307</a>
<a href="#cl-308"> 308</a>
<a href="#cl-309"> 309</a>
<a href="#cl-310"> 310</a>
<a href="#cl-311"> 311</a>
<a href="#cl-312"> 312</a>
<a href="#cl-313"> 313</a>
<a href="#cl-314"> 314</a>
<a href="#cl-315"> 315</a>
<a href="#cl-316"> 316</a>
<a href="#cl-317"> 317</a>
<a href="#cl-318"> 318</a>
<a href="#cl-319"> 319</a>
<a href="#cl-320"> 320</a>
<a href="#cl-321"> 321</a>
<a href="#cl-322"> 322</a>
<a href="#cl-323"> 323</a>
<a href="#cl-324"> 324</a>
<a href="#cl-325"> 325</a>
<a href="#cl-326"> 326</a>
<a href="#cl-327"> 327</a>
<a href="#cl-328"> 328</a>
<a href="#cl-329"> 329</a>
<a href="#cl-330"> 330</a>
<a href="#cl-331"> 331</a>
<a href="#cl-332"> 332</a>
<a href="#cl-333"> 333</a>
<a href="#cl-334"> 334</a>
<a href="#cl-335"> 335</a>
<a href="#cl-336"> 336</a>
<a href="#cl-337"> 337</a>
<a href="#cl-338"> 338</a>
<a href="#cl-339"> 339</a>
<a href="#cl-340"> 340</a>
<a href="#cl-341"> 341</a>
<a href="#cl-342"> 342</a>
<a href="#cl-343"> 343</a>
<a href="#cl-344"> 344</a>
<a href="#cl-345"> 345</a>
<a href="#cl-346"> 346</a>
<a href="#cl-347"> 347</a>
<a href="#cl-348"> 348</a>
<a href="#cl-349"> 349</a>
<a href="#cl-350"> 350</a>
<a href="#cl-351"> 351</a>
<a href="#cl-352"> 352</a>
<a href="#cl-353"> 353</a>
<a href="#cl-354"> 354</a>
<a href="#cl-355"> 355</a>
<a href="#cl-356"> 356</a>
<a href="#cl-357"> 357</a>
<a href="#cl-358"> 358</a>
<a href="#cl-359"> 359</a>
<a href="#cl-360"> 360</a>
<a href="#cl-361"> 361</a>
<a href="#cl-362"> 362</a>
<a href="#cl-363"> 363</a>
<a href="#cl-364"> 364</a>
<a href="#cl-365"> 365</a>
<a href="#cl-366"> 366</a>
<a href="#cl-367"> 367</a>
<a href="#cl-368"> 368</a>
<a href="#cl-369"> 369</a>
<a href="#cl-370"> 370</a>
<a href="#cl-371"> 371</a>
<a href="#cl-372"> 372</a>
<a href="#cl-373"> 373</a>
<a href="#cl-374"> 374</a>
<a href="#cl-375"> 375</a>
<a href="#cl-376"> 376</a>
<a href="#cl-377"> 377</a>
<a href="#cl-378"> 378</a>
<a href="#cl-379"> 379</a>
<a href="#cl-380"> 380</a>
<a href="#cl-381"> 381</a>
<a href="#cl-382"> 382</a>
<a href="#cl-383"> 383</a>
<a href="#cl-384"> 384</a>
<a href="#cl-385"> 385</a>
<a href="#cl-386"> 386</a>
<a href="#cl-387"> 387</a>
<a href="#cl-388"> 388</a>
<a href="#cl-389"> 389</a>
<a href="#cl-390"> 390</a>
<a href="#cl-391"> 391</a>
<a href="#cl-392"> 392</a>
<a href="#cl-393"> 393</a>
<a href="#cl-394"> 394</a>
<a href="#cl-395"> 395</a>
<a href="#cl-396"> 396</a>
<a href="#cl-397"> 397</a>
<a href="#cl-398"> 398</a>
<a href="#cl-399"> 399</a>
<a href="#cl-400"> 400</a>
<a href="#cl-401"> 401</a>
<a href="#cl-402"> 402</a>
<a href="#cl-403"> 403</a>
<a href="#cl-404"> 404</a>
<a href="#cl-405"> 405</a>
<a href="#cl-406"> 406</a>
<a href="#cl-407"> 407</a>
<a href="#cl-408"> 408</a>
<a href="#cl-409"> 409</a>
<a href="#cl-410"> 410</a>
<a href="#cl-411"> 411</a>
<a href="#cl-412"> 412</a>
<a href="#cl-413"> 413</a>
<a href="#cl-414"> 414</a>
<a href="#cl-415"> 415</a>
<a href="#cl-416"> 416</a>
<a href="#cl-417"> 417</a>
<a href="#cl-418"> 418</a>
<a href="#cl-419"> 419</a>
<a href="#cl-420"> 420</a>
<a href="#cl-421"> 421</a>
<a href="#cl-422"> 422</a>
<a href="#cl-423"> 423</a>
<a href="#cl-424"> 424</a>
<a href="#cl-425"> 425</a>
<a href="#cl-426"> 426</a>
<a href="#cl-427"> 427</a>
<a href="#cl-428"> 428</a>
<a href="#cl-429"> 429</a>
<a href="#cl-430"> 430</a>
<a href="#cl-431"> 431</a>
<a href="#cl-432"> 432</a>
<a href="#cl-433"> 433</a>
<a href="#cl-434"> 434</a>
<a href="#cl-435"> 435</a>
<a href="#cl-436"> 436</a>
<a href="#cl-437"> 437</a>
<a href="#cl-438"> 438</a>
<a href="#cl-439"> 439</a>
<a href="#cl-440"> 440</a>
<a href="#cl-441"> 441</a>
<a href="#cl-442"> 442</a>
<a href="#cl-443"> 443</a>
<a href="#cl-444"> 444</a>
<a href="#cl-445"> 445</a>
<a href="#cl-446"> 446</a>
<a href="#cl-447"> 447</a>
<a href="#cl-448"> 448</a>
<a href="#cl-449"> 449</a>
<a href="#cl-450"> 450</a>
<a href="#cl-451"> 451</a>
<a href="#cl-452"> 452</a>
<a href="#cl-453"> 453</a>
<a href="#cl-454"> 454</a>
<a href="#cl-455"> 455</a>
<a href="#cl-456"> 456</a>
<a href="#cl-457"> 457</a>
<a href="#cl-458"> 458</a>
<a href="#cl-459"> 459</a>
<a href="#cl-460"> 460</a>
<a href="#cl-461"> 461</a>
<a href="#cl-462"> 462</a>
<a href="#cl-463"> 463</a>
<a href="#cl-464"> 464</a>
<a href="#cl-465"> 465</a>
<a href="#cl-466"> 466</a>
<a href="#cl-467"> 467</a>
<a href="#cl-468"> 468</a>
<a href="#cl-469"> 469</a>
<a href="#cl-470"> 470</a>
<a href="#cl-471"> 471</a>
<a href="#cl-472"> 472</a>
<a href="#cl-473"> 473</a>
<a href="#cl-474"> 474</a>
<a href="#cl-475"> 475</a>
<a href="#cl-476"> 476</a>
<a href="#cl-477"> 477</a>
<a href="#cl-478"> 478</a>
<a href="#cl-479"> 479</a>
<a href="#cl-480"> 480</a>
<a href="#cl-481"> 481</a>
<a href="#cl-482"> 482</a>
<a href="#cl-483"> 483</a>
<a href="#cl-484"> 484</a>
<a href="#cl-485"> 485</a>
<a href="#cl-486"> 486</a>
<a href="#cl-487"> 487</a>
<a href="#cl-488"> 488</a>
<a href="#cl-489"> 489</a>
<a href="#cl-490"> 490</a>
<a href="#cl-491"> 491</a>
<a href="#cl-492"> 492</a>
<a href="#cl-493"> 493</a>
<a href="#cl-494"> 494</a>
<a href="#cl-495"> 495</a>
<a href="#cl-496"> 496</a>
<a href="#cl-497"> 497</a>
<a href="#cl-498"> 498</a>
<a href="#cl-499"> 499</a>
<a href="#cl-500"> 500</a>
<a href="#cl-501"> 501</a>
<a href="#cl-502"> 502</a>
<a href="#cl-503"> 503</a>
<a href="#cl-504"> 504</a>
<a href="#cl-505"> 505</a>
<a href="#cl-506"> 506</a>
<a href="#cl-507"> 507</a>
<a href="#cl-508"> 508</a>
<a href="#cl-509"> 509</a>
<a href="#cl-510"> 510</a>
<a href="#cl-511"> 511</a>
<a href="#cl-512"> 512</a>
<a href="#cl-513"> 513</a>
<a href="#cl-514"> 514</a>
<a href="#cl-515"> 515</a>
<a href="#cl-516"> 516</a>
<a href="#cl-517"> 517</a>
<a href="#cl-518"> 518</a>
<a href="#cl-519"> 519</a>
<a href="#cl-520"> 520</a>
<a href="#cl-521"> 521</a>
<a href="#cl-522"> 522</a>
<a href="#cl-523"> 523</a>
<a href="#cl-524"> 524</a>
<a href="#cl-525"> 525</a>
<a href="#cl-526"> 526</a>
<a href="#cl-527"> 527</a>
<a href="#cl-528"> 528</a>
<a href="#cl-529"> 529</a>
<a href="#cl-530"> 530</a>
<a href="#cl-531"> 531</a>
<a href="#cl-532"> 532</a>
<a href="#cl-533"> 533</a>
<a href="#cl-534"> 534</a>
<a href="#cl-535"> 535</a>
<a href="#cl-536"> 536</a>
<a href="#cl-537"> 537</a>
<a href="#cl-538"> 538</a>
<a href="#cl-539"> 539</a>
<a href="#cl-540"> 540</a>
<a href="#cl-541"> 541</a>
<a href="#cl-542"> 542</a>
<a href="#cl-543"> 543</a>
<a href="#cl-544"> 544</a>
<a href="#cl-545"> 545</a>
<a href="#cl-546"> 546</a>
<a href="#cl-547"> 547</a>
<a href="#cl-548"> 548</a>
<a href="#cl-549"> 549</a>
<a href="#cl-550"> 550</a>
<a href="#cl-551"> 551</a>
<a href="#cl-552"> 552</a>
<a href="#cl-553"> 553</a>
<a href="#cl-554"> 554</a>
<a href="#cl-555"> 555</a>
<a href="#cl-556"> 556</a>
<a href="#cl-557"> 557</a>
<a href="#cl-558"> 558</a>
<a href="#cl-559"> 559</a>
<a href="#cl-560"> 560</a>
<a href="#cl-561"> 561</a>
<a href="#cl-562"> 562</a>
<a href="#cl-563"> 563</a>
<a href="#cl-564"> 564</a>
<a href="#cl-565"> 565</a>
<a href="#cl-566"> 566</a>
<a href="#cl-567"> 567</a>
<a href="#cl-568"> 568</a>
<a href="#cl-569"> 569</a>
<a href="#cl-570"> 570</a>
<a href="#cl-571"> 571</a>
<a href="#cl-572"> 572</a>
<a href="#cl-573"> 573</a>
<a href="#cl-574"> 574</a>
<a href="#cl-575"> 575</a>
<a href="#cl-576"> 576</a>
<a href="#cl-577"> 577</a>
<a href="#cl-578"> 578</a>
<a href="#cl-579"> 579</a>
<a href="#cl-580"> 580</a>
<a href="#cl-581"> 581</a>
<a href="#cl-582"> 582</a>
<a href="#cl-583"> 583</a>
<a href="#cl-584"> 584</a>
<a href="#cl-585"> 585</a>
<a href="#cl-586"> 586</a>
<a href="#cl-587"> 587</a>
<a href="#cl-588"> 588</a>
<a href="#cl-589"> 589</a>
<a href="#cl-590"> 590</a>
<a href="#cl-591"> 591</a>
<a href="#cl-592"> 592</a>
<a href="#cl-593"> 593</a>
<a href="#cl-594"> 594</a>
<a href="#cl-595"> 595</a>
<a href="#cl-596"> 596</a>
<a href="#cl-597"> 597</a>
<a href="#cl-598"> 598</a>
<a href="#cl-599"> 599</a>
<a href="#cl-600"> 600</a>
<a href="#cl-601"> 601</a>
<a href="#cl-602"> 602</a>
<a href="#cl-603"> 603</a>
<a href="#cl-604"> 604</a>
<a href="#cl-605"> 605</a>
<a href="#cl-606"> 606</a>
<a href="#cl-607"> 607</a>
<a href="#cl-608"> 608</a>
<a href="#cl-609"> 609</a>
<a href="#cl-610"> 610</a>
<a href="#cl-611"> 611</a>
<a href="#cl-612"> 612</a>
<a href="#cl-613"> 613</a>
<a href="#cl-614"> 614</a>
<a href="#cl-615"> 615</a>
<a href="#cl-616"> 616</a>
<a href="#cl-617"> 617</a>
<a href="#cl-618"> 618</a>
<a href="#cl-619"> 619</a>
<a href="#cl-620"> 620</a>
<a href="#cl-621"> 621</a>
<a href="#cl-622"> 622</a>
<a href="#cl-623"> 623</a>
<a href="#cl-624"> 624</a>
<a href="#cl-625"> 625</a>
<a href="#cl-626"> 626</a>
<a href="#cl-627"> 627</a>
<a href="#cl-628"> 628</a>
<a href="#cl-629"> 629</a>
<a href="#cl-630"> 630</a>
<a href="#cl-631"> 631</a>
<a href="#cl-632"> 632</a>
<a href="#cl-633"> 633</a>
<a href="#cl-634"> 634</a>
<a href="#cl-635"> 635</a>
<a href="#cl-636"> 636</a>
<a href="#cl-637"> 637</a>
<a href="#cl-638"> 638</a>
<a href="#cl-639"> 639</a>
<a href="#cl-640"> 640</a>
<a href="#cl-641"> 641</a>
<a href="#cl-642"> 642</a>
<a href="#cl-643"> 643</a>
<a href="#cl-644"> 644</a>
<a href="#cl-645"> 645</a>
<a href="#cl-646"> 646</a>
<a href="#cl-647"> 647</a>
<a href="#cl-648"> 648</a>
<a href="#cl-649"> 649</a>
<a href="#cl-650"> 650</a>
<a href="#cl-651"> 651</a>
<a href="#cl-652"> 652</a>
<a href="#cl-653"> 653</a>
<a href="#cl-654"> 654</a>
<a href="#cl-655"> 655</a>
<a href="#cl-656"> 656</a>
<a href="#cl-657"> 657</a>
<a href="#cl-658"> 658</a>
<a href="#cl-659"> 659</a>
<a href="#cl-660"> 660</a>
<a href="#cl-661"> 661</a>
<a href="#cl-662"> 662</a>
<a href="#cl-663"> 663</a>
<a href="#cl-664"> 664</a>
<a href="#cl-665"> 665</a>
<a href="#cl-666"> 666</a>
<a href="#cl-667"> 667</a>
<a href="#cl-668"> 668</a>
<a href="#cl-669"> 669</a>
<a href="#cl-670"> 670</a>
<a href="#cl-671"> 671</a>
<a href="#cl-672"> 672</a>
<a href="#cl-673"> 673</a>
<a href="#cl-674"> 674</a>
<a href="#cl-675"> 675</a>
<a href="#cl-676"> 676</a>
<a href="#cl-677"> 677</a>
<a href="#cl-678"> 678</a>
<a href="#cl-679"> 679</a>
<a href="#cl-680"> 680</a>
<a href="#cl-681"> 681</a>
<a href="#cl-682"> 682</a>
<a href="#cl-683"> 683</a>
<a href="#cl-684"> 684</a>
<a href="#cl-685"> 685</a>
<a href="#cl-686"> 686</a>
<a href="#cl-687"> 687</a>
<a href="#cl-688"> 688</a>
<a href="#cl-689"> 689</a>
<a href="#cl-690"> 690</a>
<a href="#cl-691"> 691</a>
<a href="#cl-692"> 692</a>
<a href="#cl-693"> 693</a>
<a href="#cl-694"> 694</a>
<a href="#cl-695"> 695</a>
<a href="#cl-696"> 696</a>
<a href="#cl-697"> 697</a>
<a href="#cl-698"> 698</a>
<a href="#cl-699"> 699</a>
<a href="#cl-700"> 700</a>
<a href="#cl-701"> 701</a>
<a href="#cl-702"> 702</a>
<a href="#cl-703"> 703</a>
<a href="#cl-704"> 704</a>
<a href="#cl-705"> 705</a>
<a href="#cl-706"> 706</a>
<a href="#cl-707"> 707</a>
<a href="#cl-708"> 708</a>
<a href="#cl-709"> 709</a>
<a href="#cl-710"> 710</a>
<a href="#cl-711"> 711</a>
<a href="#cl-712"> 712</a>
<a href="#cl-713"> 713</a>
<a href="#cl-714"> 714</a>
<a href="#cl-715"> 715</a>
<a href="#cl-716"> 716</a>
<a href="#cl-717"> 717</a>
<a href="#cl-718"> 718</a>
<a href="#cl-719"> 719</a>
<a href="#cl-720"> 720</a>
<a href="#cl-721"> 721</a>
<a href="#cl-722"> 722</a>
<a href="#cl-723"> 723</a>
<a href="#cl-724"> 724</a>
<a href="#cl-725"> 725</a>
<a href="#cl-726"> 726</a>
<a href="#cl-727"> 727</a>
<a href="#cl-728"> 728</a>
<a href="#cl-729"> 729</a>
<a href="#cl-730"> 730</a>
<a href="#cl-731"> 731</a>
<a href="#cl-732"> 732</a>
<a href="#cl-733"> 733</a>
<a href="#cl-734"> 734</a>
<a href="#cl-735"> 735</a>
<a href="#cl-736"> 736</a>
<a href="#cl-737"> 737</a>
<a href="#cl-738"> 738</a>
<a href="#cl-739"> 739</a>
<a href="#cl-740"> 740</a>
<a href="#cl-741"> 741</a>
<a href="#cl-742"> 742</a>
<a href="#cl-743"> 743</a>
<a href="#cl-744"> 744</a>
<a href="#cl-745"> 745</a>
<a href="#cl-746"> 746</a>
<a href="#cl-747"> 747</a>
<a href="#cl-748"> 748</a>
<a href="#cl-749"> 749</a>
<a href="#cl-750"> 750</a>
<a href="#cl-751"> 751</a>
<a href="#cl-752"> 752</a>
<a href="#cl-753"> 753</a>
<a href="#cl-754"> 754</a>
<a href="#cl-755"> 755</a>
<a href="#cl-756"> 756</a>
<a href="#cl-757"> 757</a>
<a href="#cl-758"> 758</a>
<a href="#cl-759"> 759</a>
<a href="#cl-760"> 760</a>
<a href="#cl-761"> 761</a>
<a href="#cl-762"> 762</a>
<a href="#cl-763"> 763</a>
<a href="#cl-764"> 764</a>
<a href="#cl-765"> 765</a>
<a href="#cl-766"> 766</a>
<a href="#cl-767"> 767</a>
<a href="#cl-768"> 768</a>
<a href="#cl-769"> 769</a>
<a href="#cl-770"> 770</a>
<a href="#cl-771"> 771</a>
<a href="#cl-772"> 772</a>
<a href="#cl-773"> 773</a>
<a href="#cl-774"> 774</a>
<a href="#cl-775"> 775</a>
<a href="#cl-776"> 776</a>
<a href="#cl-777"> 777</a>
<a href="#cl-778"> 778</a>
<a href="#cl-779"> 779</a>
<a href="#cl-780"> 780</a>
<a href="#cl-781"> 781</a>
<a href="#cl-782"> 782</a>
<a href="#cl-783"> 783</a>
<a href="#cl-784"> 784</a>
<a href="#cl-785"> 785</a>
<a href="#cl-786"> 786</a>
<a href="#cl-787"> 787</a>
<a href="#cl-788"> 788</a>
<a href="#cl-789"> 789</a>
<a href="#cl-790"> 790</a>
<a href="#cl-791"> 791</a>
<a href="#cl-792"> 792</a>
<a href="#cl-793"> 793</a>
<a href="#cl-794"> 794</a>
<a href="#cl-795"> 795</a>
<a href="#cl-796"> 796</a>
<a href="#cl-797"> 797</a>
<a href="#cl-798"> 798</a>
<a href="#cl-799"> 799</a>
<a href="#cl-800"> 800</a>
<a href="#cl-801"> 801</a>
<a href="#cl-802"> 802</a>
<a href="#cl-803"> 803</a>
<a href="#cl-804"> 804</a>
<a href="#cl-805"> 805</a>
<a href="#cl-806"> 806</a>
<a href="#cl-807"> 807</a>
<a href="#cl-808"> 808</a>
<a href="#cl-809"> 809</a>
<a href="#cl-810"> 810</a>
<a href="#cl-811"> 811</a>
<a href="#cl-812"> 812</a>
<a href="#cl-813"> 813</a>
<a href="#cl-814"> 814</a>
<a href="#cl-815"> 815</a>
<a href="#cl-816"> 816</a>
<a href="#cl-817"> 817</a>
<a href="#cl-818"> 818</a>
<a href="#cl-819"> 819</a>
<a href="#cl-820"> 820</a>
<a href="#cl-821"> 821</a>
<a href="#cl-822"> 822</a>
<a href="#cl-823"> 823</a>
<a href="#cl-824"> 824</a>
<a href="#cl-825"> 825</a>
<a href="#cl-826"> 826</a>
<a href="#cl-827"> 827</a>
<a href="#cl-828"> 828</a>
<a href="#cl-829"> 829</a>
<a href="#cl-830"> 830</a>
<a href="#cl-831"> 831</a>
<a href="#cl-832"> 832</a>
<a href="#cl-833"> 833</a>
<a href="#cl-834"> 834</a>
<a href="#cl-835"> 835</a>
<a href="#cl-836"> 836</a>
<a href="#cl-837"> 837</a>
<a href="#cl-838"> 838</a>
<a href="#cl-839"> 839</a>
<a href="#cl-840"> 840</a>
<a href="#cl-841"> 841</a>
<a href="#cl-842"> 842</a>
<a href="#cl-843"> 843</a>
<a href="#cl-844"> 844</a>
<a href="#cl-845"> 845</a>
<a href="#cl-846"> 846</a>
<a href="#cl-847"> 847</a>
<a href="#cl-848"> 848</a>
<a href="#cl-849"> 849</a>
<a href="#cl-850"> 850</a>
<a href="#cl-851"> 851</a>
<a href="#cl-852"> 852</a>
<a href="#cl-853"> 853</a>
<a href="#cl-854"> 854</a>
<a href="#cl-855"> 855</a>
<a href="#cl-856"> 856</a>
<a href="#cl-857"> 857</a>
<a href="#cl-858"> 858</a>
<a href="#cl-859"> 859</a>
<a href="#cl-860"> 860</a>
<a href="#cl-861"> 861</a>
<a href="#cl-862"> 862</a>
<a href="#cl-863"> 863</a>
<a href="#cl-864"> 864</a>
<a href="#cl-865"> 865</a>
<a href="#cl-866"> 866</a>
<a href="#cl-867"> 867</a>
<a href="#cl-868"> 868</a>
<a href="#cl-869"> 869</a>
<a href="#cl-870"> 870</a>
<a href="#cl-871"> 871</a>
<a href="#cl-872"> 872</a>
<a href="#cl-873"> 873</a>
<a href="#cl-874"> 874</a>
<a href="#cl-875"> 875</a>
<a href="#cl-876"> 876</a>
<a href="#cl-877"> 877</a>
<a href="#cl-878"> 878</a>
<a href="#cl-879"> 879</a>
<a href="#cl-880"> 880</a>
<a href="#cl-881"> 881</a>
<a href="#cl-882"> 882</a>
<a href="#cl-883"> 883</a>
<a href="#cl-884"> 884</a>
<a href="#cl-885"> 885</a>
<a href="#cl-886"> 886</a>
<a href="#cl-887"> 887</a>
<a href="#cl-888"> 888</a>
<a href="#cl-889"> 889</a>
<a href="#cl-890"> 890</a>
<a href="#cl-891"> 891</a>
<a href="#cl-892"> 892</a>
<a href="#cl-893"> 893</a>
<a href="#cl-894"> 894</a>
<a href="#cl-895"> 895</a>
<a href="#cl-896"> 896</a>
<a href="#cl-897"> 897</a>
<a href="#cl-898"> 898</a>
<a href="#cl-899"> 899</a>
<a href="#cl-900"> 900</a>
<a href="#cl-901"> 901</a>
<a href="#cl-902"> 902</a>
<a href="#cl-903"> 903</a>
<a href="#cl-904"> 904</a>
<a href="#cl-905"> 905</a>
<a href="#cl-906"> 906</a>
<a href="#cl-907"> 907</a>
<a href="#cl-908"> 908</a>
<a href="#cl-909"> 909</a>
<a href="#cl-910"> 910</a>
<a href="#cl-911"> 911</a>
<a href="#cl-912"> 912</a>
<a href="#cl-913"> 913</a>
<a href="#cl-914"> 914</a>
<a href="#cl-915"> 915</a>
<a href="#cl-916"> 916</a>
<a href="#cl-917"> 917</a>
<a href="#cl-918"> 918</a>
<a href="#cl-919"> 919</a>
<a href="#cl-920"> 920</a>
<a href="#cl-921"> 921</a>
<a href="#cl-922"> 922</a>
<a href="#cl-923"> 923</a>
<a href="#cl-924"> 924</a>
<a href="#cl-925"> 925</a>
<a href="#cl-926"> 926</a>
<a href="#cl-927"> 927</a>
<a href="#cl-928"> 928</a>
<a href="#cl-929"> 929</a>
<a href="#cl-930"> 930</a>
<a href="#cl-931"> 931</a>
<a href="#cl-932"> 932</a>
<a href="#cl-933"> 933</a>
<a href="#cl-934"> 934</a>
<a href="#cl-935"> 935</a>
<a href="#cl-936"> 936</a>
<a href="#cl-937"> 937</a>
<a href="#cl-938"> 938</a>
<a href="#cl-939"> 939</a>
<a href="#cl-940"> 940</a>
<a href="#cl-941"> 941</a>
<a href="#cl-942"> 942</a>
<a href="#cl-943"> 943</a>
<a href="#cl-944"> 944</a>
<a href="#cl-945"> 945</a>
<a href="#cl-946"> 946</a>
<a href="#cl-947"> 947</a>
<a href="#cl-948"> 948</a>
<a href="#cl-949"> 949</a>
<a href="#cl-950"> 950</a>
<a href="#cl-951"> 951</a>
<a href="#cl-952"> 952</a>
<a href="#cl-953"> 953</a>
<a href="#cl-954"> 954</a>
<a href="#cl-955"> 955</a>
<a href="#cl-956"> 956</a>
<a href="#cl-957"> 957</a>
<a href="#cl-958"> 958</a>
<a href="#cl-959"> 959</a>
<a href="#cl-960"> 960</a>
<a href="#cl-961"> 961</a>
<a href="#cl-962"> 962</a>
<a href="#cl-963"> 963</a>
<a href="#cl-964"> 964</a>
<a href="#cl-965"> 965</a>
<a href="#cl-966"> 966</a>
<a href="#cl-967"> 967</a>
<a href="#cl-968"> 968</a>
<a href="#cl-969"> 969</a>
<a href="#cl-970"> 970</a>
<a href="#cl-971"> 971</a>
<a href="#cl-972"> 972</a>
<a href="#cl-973"> 973</a>
<a href="#cl-974"> 974</a>
<a href="#cl-975"> 975</a>
<a href="#cl-976"> 976</a>
<a href="#cl-977"> 977</a>
<a href="#cl-978"> 978</a>
<a href="#cl-979"> 979</a>
<a href="#cl-980"> 980</a>
<a href="#cl-981"> 981</a>
<a href="#cl-982"> 982</a>
<a href="#cl-983"> 983</a>
<a href="#cl-984"> 984</a>
<a href="#cl-985"> 985</a>
<a href="#cl-986"> 986</a>
<a href="#cl-987"> 987</a>
<a href="#cl-988"> 988</a>
<a href="#cl-989"> 989</a>
<a href="#cl-990"> 990</a>
<a href="#cl-991"> 991</a>
<a href="#cl-992"> 992</a>
<a href="#cl-993"> 993</a>
<a href="#cl-994"> 994</a>
<a href="#cl-995"> 995</a>
<a href="#cl-996"> 996</a>
<a href="#cl-997"> 997</a>
<a href="#cl-998"> 998</a>
<a href="#cl-999"> 999</a>
<a href="#cl-1000">1000</a>
<a href="#cl-1001">1001</a>
<a href="#cl-1002">1002</a>
<a href="#cl-1003">1003</a>
<a href="#cl-1004">1004</a>
<a href="#cl-1005">1005</a>
<a href="#cl-1006">1006</a>
<a href="#cl-1007">1007</a>
<a href="#cl-1008">1008</a>
<a href="#cl-1009">1009</a>
<a href="#cl-1010">1010</a>
<a href="#cl-1011">1011</a>
<a href="#cl-1012">1012</a>
<a href="#cl-1013">1013</a>
<a href="#cl-1014">1014</a>
<a href="#cl-1015">1015</a>
<a href="#cl-1016">1016</a>
<a href="#cl-1017">1017</a>
<a href="#cl-1018">1018</a>
<a href="#cl-1019">1019</a>
<a href="#cl-1020">1020</a>
<a href="#cl-1021">1021</a>
<a href="#cl-1022">1022</a>
<a href="#cl-1023">1023</a>
<a href="#cl-1024">1024</a>
<a href="#cl-1025">1025</a>
<a href="#cl-1026">1026</a>
<a href="#cl-1027">1027</a>
<a href="#cl-1028">1028</a>
<a href="#cl-1029">1029</a>
<a href="#cl-1030">1030</a>
<a href="#cl-1031">1031</a>
<a href="#cl-1032">1032</a>
<a href="#cl-1033">1033</a>
<a href="#cl-1034">1034</a>
<a href="#cl-1035">1035</a>
<a href="#cl-1036">1036</a>
<a href="#cl-1037">1037</a>
<a href="#cl-1038">1038</a>
<a href="#cl-1039">1039</a>
<a href="#cl-1040">1040</a>
<a href="#cl-1041">1041</a>
<a href="#cl-1042">1042</a>
<a href="#cl-1043">1043</a>
<a href="#cl-1044">1044</a>
<a href="#cl-1045">1045</a>
<a href="#cl-1046">1046</a>
<a href="#cl-1047">1047</a>
<a href="#cl-1048">1048</a>
<a href="#cl-1049">1049</a>
<a href="#cl-1050">1050</a>
<a href="#cl-1051">1051</a>
<a href="#cl-1052">1052</a>
<a href="#cl-1053">1053</a>
<a href="#cl-1054">1054</a>
<a href="#cl-1055">1055</a>
<a href="#cl-1056">1056</a>
<a href="#cl-1057">1057</a>
<a href="#cl-1058">1058</a>
<a href="#cl-1059">1059</a>
<a href="#cl-1060">1060</a>
<a href="#cl-1061">1061</a>
<a href="#cl-1062">1062</a>
<a href="#cl-1063">1063</a>
<a href="#cl-1064">1064</a>
<a href="#cl-1065">1065</a>
<a href="#cl-1066">1066</a>
<a href="#cl-1067">1067</a>
<a href="#cl-1068">1068</a>
<a href="#cl-1069">1069</a>
<a href="#cl-1070">1070</a>
<a href="#cl-1071">1071</a>
<a href="#cl-1072">1072</a>
<a href="#cl-1073">1073</a>
<a href="#cl-1074">1074</a>
<a href="#cl-1075">1075</a>
<a href="#cl-1076">1076</a>
<a href="#cl-1077">1077</a>
<a href="#cl-1078">1078</a>
<a href="#cl-1079">1079</a>
<a href="#cl-1080">1080</a>
<a href="#cl-1081">1081</a>
<a href="#cl-1082">1082</a>
<a href="#cl-1083">1083</a>
<a href="#cl-1084">1084</a>
<a href="#cl-1085">1085</a>
<a href="#cl-1086">1086</a>
<a href="#cl-1087">1087</a>
<a href="#cl-1088">1088</a>
<a href="#cl-1089">1089</a>
<a href="#cl-1090">1090</a>
<a href="#cl-1091">1091</a>
<a href="#cl-1092">1092</a>
<a href="#cl-1093">1093</a>
<a href="#cl-1094">1094</a>
<a href="#cl-1095">1095</a>
<a href="#cl-1096">1096</a>
<a href="#cl-1097">1097</a>
<a href="#cl-1098">1098</a>
<a href="#cl-1099">1099</a>
<a href="#cl-1100">1100</a>
<a href="#cl-1101">1101</a>
<a href="#cl-1102">1102</a>
<a href="#cl-1103">1103</a>
<a href="#cl-1104">1104</a>
<a href="#cl-1105">1105</a>
<a href="#cl-1106">1106</a>
<a href="#cl-1107">1107</a>
<a href="#cl-1108">1108</a>
<a href="#cl-1109">1109</a>
<a href="#cl-1110">1110</a>
<a href="#cl-1111">1111</a>
<a href="#cl-1112">1112</a>
<a href="#cl-1113">1113</a>
<a href="#cl-1114">1114</a>
<a href="#cl-1115">1115</a>
<a href="#cl-1116">1116</a>
<a href="#cl-1117">1117</a>
<a href="#cl-1118">1118</a>
<a href="#cl-1119">1119</a>
<a href="#cl-1120">1120</a>
<a href="#cl-1121">1121</a>
<a href="#cl-1122">1122</a>
<a href="#cl-1123">1123</a>
<a href="#cl-1124">1124</a>
<a href="#cl-1125">1125</a>
<a href="#cl-1126">1126</a>
<a href="#cl-1127">1127</a>
<a href="#cl-1128">1128</a>
<a href="#cl-1129">1129</a>
<a href="#cl-1130">1130</a>
<a href="#cl-1131">1131</a>
<a href="#cl-1132">1132</a>
<a href="#cl-1133">1133</a>
<a href="#cl-1134">1134</a>
<a href="#cl-1135">1135</a>
<a href="#cl-1136">1136</a>
<a href="#cl-1137">1137</a>
<a href="#cl-1138">1138</a>
<a href="#cl-1139">1139</a>
<a href="#cl-1140">1140</a>
<a href="#cl-1141">1141</a>
<a href="#cl-1142">1142</a>
<a href="#cl-1143">1143</a>
<a href="#cl-1144">1144</a>
<a href="#cl-1145">1145</a>
<a href="#cl-1146">1146</a>
<a href="#cl-1147">1147</a>
<a href="#cl-1148">1148</a>
<a href="#cl-1149">1149</a>
<a href="#cl-1150">1150</a>
<a href="#cl-1151">1151</a>
<a href="#cl-1152">1152</a>
<a href="#cl-1153">1153</a>
<a href="#cl-1154">1154</a>
<a href="#cl-1155">1155</a>
<a href="#cl-1156">1156</a>
<a href="#cl-1157">1157</a>
<a href="#cl-1158">1158</a>
<a href="#cl-1159">1159</a>
<a href="#cl-1160">1160</a>
<a href="#cl-1161">1161</a>
<a href="#cl-1162">1162</a>
<a href="#cl-1163">1163</a>
<a href="#cl-1164">1164</a>
<a href="#cl-1165">1165</a>
<a href="#cl-1166">1166</a>
<a href="#cl-1167">1167</a>
<a href="#cl-1168">1168</a>
<a href="#cl-1169">1169</a>
<a href="#cl-1170">1170</a>
<a href="#cl-1171">1171</a>
<a href="#cl-1172">1172</a>
<a href="#cl-1173">1173</a>
<a href="#cl-1174">1174</a>
<a href="#cl-1175">1175</a>
<a href="#cl-1176">1176</a>
<a href="#cl-1177">1177</a>
<a href="#cl-1178">1178</a>
<a href="#cl-1179">1179</a>
<a href="#cl-1180">1180</a>
<a href="#cl-1181">1181</a>
<a href="#cl-1182">1182</a>
<a href="#cl-1183">1183</a>
<a href="#cl-1184">1184</a>
<a href="#cl-1185">1185</a>
<a href="#cl-1186">1186</a>
<a href="#cl-1187">1187</a>
<a href="#cl-1188">1188</a>
<a href="#cl-1189">1189</a>
<a href="#cl-1190">1190</a>
<a href="#cl-1191">1191</a>
<a href="#cl-1192">1192</a>
<a href="#cl-1193">1193</a>
<a href="#cl-1194">1194</a>
<a href="#cl-1195">1195</a>
<a href="#cl-1196">1196</a>
<a href="#cl-1197">1197</a>
<a href="#cl-1198">1198</a>
<a href="#cl-1199">1199</a>
<a href="#cl-1200">1200</a>
<a href="#cl-1201">1201</a>
<a href="#cl-1202">1202</a>
<a href="#cl-1203">1203</a>
<a href="#cl-1204">1204</a>
<a href="#cl-1205">1205</a>
<a href="#cl-1206">1206</a>
<a href="#cl-1207">1207</a>
<a href="#cl-1208">1208</a>
<a href="#cl-1209">1209</a>
<a href="#cl-1210">1210</a>
<a href="#cl-1211">1211</a>
<a href="#cl-1212">1212</a>
<a href="#cl-1213">1213</a>
<a href="#cl-1214">1214</a>
<a href="#cl-1215">1215</a>
<a href="#cl-1216">1216</a>
<a href="#cl-1217">1217</a>
<a href="#cl-1218">1218</a>
<a href="#cl-1219">1219</a>
<a href="#cl-1220">1220</a>
<a href="#cl-1221">1221</a>
<a href="#cl-1222">1222</a>
<a href="#cl-1223">1223</a>
<a href="#cl-1224">1224</a>
<a href="#cl-1225">1225</a>
<a href="#cl-1226">1226</a>
<a href="#cl-1227">1227</a>
<a href="#cl-1228">1228</a>
<a href="#cl-1229">1229</a>
<a href="#cl-1230">1230</a>
<a href="#cl-1231">1231</a>
<a href="#cl-1232">1232</a>
<a href="#cl-1233">1233</a>
<a href="#cl-1234">1234</a>
<a href="#cl-1235">1235</a>
<a href="#cl-1236">1236</a>
<a href="#cl-1237">1237</a>
<a href="#cl-1238">1238</a>
<a href="#cl-1239">1239</a>
<a href="#cl-1240">1240</a>
<a href="#cl-1241">1241</a>
<a href="#cl-1242">1242</a>
<a href="#cl-1243">1243</a>
<a href="#cl-1244">1244</a>
<a href="#cl-1245">1245</a>
<a href="#cl-1246">1246</a>
<a href="#cl-1247">1247</a>
<a href="#cl-1248">1248</a>
<a href="#cl-1249">1249</a>
<a href="#cl-1250">1250</a>
<a href="#cl-1251">1251</a>
<a href="#cl-1252">1252</a>
<a href="#cl-1253">1253</a>
<a href="#cl-1254">1254</a>
<a href="#cl-1255">1255</a>
<a href="#cl-1256">1256</a>
<a href="#cl-1257">1257</a>
<a href="#cl-1258">1258</a>
<a href="#cl-1259">1259</a>
<a href="#cl-1260">1260</a>
<a href="#cl-1261">1261</a>
<a href="#cl-1262">1262</a>
<a href="#cl-1263">1263</a>
<a href="#cl-1264">1264</a>
<a href="#cl-1265">1265</a>
<a href="#cl-1266">1266</a>
<a href="#cl-1267">1267</a>
<a href="#cl-1268">1268</a>
<a href="#cl-1269">1269</a>
<a href="#cl-1270">1270</a>
<a href="#cl-1271">1271</a>
<a href="#cl-1272">1272</a>
<a href="#cl-1273">1273</a>
<a href="#cl-1274">1274</a>
<a href="#cl-1275">1275</a>
<a href="#cl-1276">1276</a>
<a href="#cl-1277">1277</a>
<a href="#cl-1278">1278</a>
<a href="#cl-1279">1279</a>
<a href="#cl-1280">1280</a>
<a href="#cl-1281">1281</a>
<a href="#cl-1282">1282</a>
<a href="#cl-1283">1283</a>
<a href="#cl-1284">1284</a>
<a href="#cl-1285">1285</a>
<a href="#cl-1286">1286</a>
<a href="#cl-1287">1287</a>
<a href="#cl-1288">1288</a>
<a href="#cl-1289">1289</a>
<a href="#cl-1290">1290</a>
<a href="#cl-1291">1291</a>
<a href="#cl-1292">1292</a>
<a href="#cl-1293">1293</a>
<a href="#cl-1294">1294</a>
<a href="#cl-1295">1295</a>
<a href="#cl-1296">1296</a>
<a href="#cl-1297">1297</a>
<a href="#cl-1298">1298</a>
<a href="#cl-1299">1299</a>
<a href="#cl-1300">1300</a>
<a href="#cl-1301">1301</a>
<a href="#cl-1302">1302</a>
<a href="#cl-1303">1303</a>
<a href="#cl-1304">1304</a>
<a href="#cl-1305">1305</a>
<a href="#cl-1306">1306</a>
<a href="#cl-1307">1307</a>
<a href="#cl-1308">1308</a>
<a href="#cl-1309">1309</a>
<a href="#cl-1310">1310</a>
<a href="#cl-1311">1311</a>
<a href="#cl-1312">1312</a>
<a href="#cl-1313">1313</a>
<a href="#cl-1314">1314</a>
<a href="#cl-1315">1315</a>
<a href="#cl-1316">1316</a>
<a href="#cl-1317">1317</a>
<a href="#cl-1318">1318</a>
<a href="#cl-1319">1319</a>
<a href="#cl-1320">1320</a>
<a href="#cl-1321">1321</a>
<a href="#cl-1322">1322</a>
<a href="#cl-1323">1323</a>
<a href="#cl-1324">1324</a>
<a href="#cl-1325">1325</a>
<a href="#cl-1326">1326</a>
<a href="#cl-1327">1327</a>
<a href="#cl-1328">1328</a>
<a href="#cl-1329">1329</a>
<a href="#cl-1330">1330</a>
<a href="#cl-1331">1331</a>
<a href="#cl-1332">1332</a>
<a href="#cl-1333">1333</a>
<a href="#cl-1334">1334</a>
<a href="#cl-1335">1335</a>
<a href="#cl-1336">1336</a>
<a href="#cl-1337">1337</a>
<a href="#cl-1338">1338</a>
<a href="#cl-1339">1339</a>
<a href="#cl-1340">1340</a>
<a href="#cl-1341">1341</a>
<a href="#cl-1342">1342</a>
<a href="#cl-1343">1343</a>
<a href="#cl-1344">1344</a>
<a href="#cl-1345">1345</a>
<a href="#cl-1346">1346</a>
<a href="#cl-1347">1347</a>
<a href="#cl-1348">1348</a>
<a href="#cl-1349">1349</a>
<a href="#cl-1350">1350</a>
<a href="#cl-1351">1351</a>
<a href="#cl-1352">1352</a>
<a href="#cl-1353">1353</a>
<a href="#cl-1354">1354</a>
<a href="#cl-1355">1355</a>
<a href="#cl-1356">1356</a>
<a href="#cl-1357">1357</a>
<a href="#cl-1358">1358</a>
<a href="#cl-1359">1359</a>
<a href="#cl-1360">1360</a>
<a href="#cl-1361">1361</a>
<a href="#cl-1362">1362</a>
<a href="#cl-1363">1363</a>
<a href="#cl-1364">1364</a>
<a href="#cl-1365">1365</a>
<a href="#cl-1366">1366</a>
<a href="#cl-1367">1367</a>
<a href="#cl-1368">1368</a>
<a href="#cl-1369">1369</a>
<a href="#cl-1370">1370</a>
<a href="#cl-1371">1371</a>
<a href="#cl-1372">1372</a>
<a href="#cl-1373">1373</a>
<a href="#cl-1374">1374</a>
<a href="#cl-1375">1375</a>
<a href="#cl-1376">1376</a>
<a href="#cl-1377">1377</a>
<a href="#cl-1378">1378</a>
<a href="#cl-1379">1379</a>
<a href="#cl-1380">1380</a>
<a href="#cl-1381">1381</a>
<a href="#cl-1382">1382</a>
<a href="#cl-1383">1383</a>
<a href="#cl-1384">1384</a>
<a href="#cl-1385">1385</a>
<a href="#cl-1386">1386</a>
<a href="#cl-1387">1387</a>
<a href="#cl-1388">1388</a>
<a href="#cl-1389">1389</a>
<a href="#cl-1390">1390</a>
<a href="#cl-1391">1391</a>
<a href="#cl-1392">1392</a>
<a href="#cl-1393">1393</a>
<a href="#cl-1394">1394</a>
<a href="#cl-1395">1395</a>
<a href="#cl-1396">1396</a>
<a href="#cl-1397">1397</a>
<a href="#cl-1398">1398</a>
<a href="#cl-1399">1399</a>
<a href="#cl-1400">1400</a>
<a href="#cl-1401">1401</a>
<a href="#cl-1402">1402</a>
<a href="#cl-1403">1403</a>
<a href="#cl-1404">1404</a>
<a href="#cl-1405">1405</a>
<a href="#cl-1406">1406</a>
<a href="#cl-1407">1407</a>
<a href="#cl-1408">1408</a>
<a href="#cl-1409">1409</a>
<a href="#cl-1410">1410</a>
<a href="#cl-1411">1411</a>
<a href="#cl-1412">1412</a>
<a href="#cl-1413">1413</a>
<a href="#cl-1414">1414</a>
<a href="#cl-1415">1415</a>
<a href="#cl-1416">1416</a>
<a href="#cl-1417">1417</a>
<a href="#cl-1418">1418</a>
<a href="#cl-1419">1419</a>
<a href="#cl-1420">1420</a>
<a href="#cl-1421">1421</a>
<a href="#cl-1422">1422</a>
<a href="#cl-1423">1423</a>
<a href="#cl-1424">1424</a>
<a href="#cl-1425">1425</a>
<a href="#cl-1426">1426</a>
<a href="#cl-1427">1427</a>
<a href="#cl-1428">1428</a>
<a href="#cl-1429">1429</a>
<a href="#cl-1430">1430</a>
<a href="#cl-1431">1431</a>
<a href="#cl-1432">1432</a>
<a href="#cl-1433">1433</a>
<a href="#cl-1434">1434</a>
<a href="#cl-1435">1435</a>
<a href="#cl-1436">1436</a>
<a href="#cl-1437">1437</a>
<a href="#cl-1438">1438</a>
<a href="#cl-1439">1439</a>
<a href="#cl-1440">1440</a>
<a href="#cl-1441">1441</a>
<a href="#cl-1442">1442</a>
<a href="#cl-1443">1443</a>
<a href="#cl-1444">1444</a>
<a href="#cl-1445">1445</a>
<a href="#cl-1446">1446</a>
<a href="#cl-1447">1447</a>
<a href="#cl-1448">1448</a>
<a href="#cl-1449">1449</a>
<a href="#cl-1450">1450</a>
<a href="#cl-1451">1451</a>
<a href="#cl-1452">1452</a>
<a href="#cl-1453">1453</a>
<a href="#cl-1454">1454</a>
<a href="#cl-1455">1455</a>
<a href="#cl-1456">1456</a>
<a href="#cl-1457">1457</a>
<a href="#cl-1458">1458</a>
<a href="#cl-1459">1459</a>
<a href="#cl-1460">1460</a>
<a href="#cl-1461">1461</a>
<a href="#cl-1462">1462</a>
<a href="#cl-1463">1463</a>
<a href="#cl-1464">1464</a>
<a href="#cl-1465">1465</a>
<a href="#cl-1466">1466</a>
<a href="#cl-1467">1467</a>
<a href="#cl-1468">1468</a>
<a href="#cl-1469">1469</a>
<a href="#cl-1470">1470</a>
<a href="#cl-1471">1471</a>
<a href="#cl-1472">1472</a>
<a href="#cl-1473">1473</a>
<a href="#cl-1474">1474</a>
<a href="#cl-1475">1475</a>
<a href="#cl-1476">1476</a>
<a href="#cl-1477">1477</a>
<a href="#cl-1478">1478</a>
<a href="#cl-1479">1479</a>
<a href="#cl-1480">1480</a>
<a href="#cl-1481">1481</a>
<a href="#cl-1482">1482</a>
<a href="#cl-1483">1483</a>
<a href="#cl-1484">1484</a>
<a href="#cl-1485">1485</a>
<a href="#cl-1486">1486</a>
<a href="#cl-1487">1487</a>
<a href="#cl-1488">1488</a>
<a href="#cl-1489">1489</a>
<a href="#cl-1490">1490</a>
<a href="#cl-1491">1491</a>
<a href="#cl-1492">1492</a>
<a href="#cl-1493">1493</a>
<a href="#cl-1494">1494</a>
<a href="#cl-1495">1495</a>
<a href="#cl-1496">1496</a>
<a href="#cl-1497">1497</a>
<a href="#cl-1498">1498</a>
<a href="#cl-1499">1499</a>
<a href="#cl-1500">1500</a>
<a href="#cl-1501">1501</a>
<a href="#cl-1502">1502</a>
<a href="#cl-1503">1503</a>
<a href="#cl-1504">1504</a>
<a href="#cl-1505">1505</a>
<a href="#cl-1506">1506</a>
<a href="#cl-1507">1507</a>
<a href="#cl-1508">1508</a>
<a href="#cl-1509">1509</a>
<a href="#cl-1510">1510</a>
<a href="#cl-1511">1511</a>
<a href="#cl-1512">1512</a>
<a href="#cl-1513">1513</a>
<a href="#cl-1514">1514</a>
<a href="#cl-1515">1515</a>
<a href="#cl-1516">1516</a>
<a href="#cl-1517">1517</a>
<a href="#cl-1518">1518</a>
<a href="#cl-1519">1519</a>
<a href="#cl-1520">1520</a>
<a href="#cl-1521">1521</a>
<a href="#cl-1522">1522</a>
<a href="#cl-1523">1523</a>
<a href="#cl-1524">1524</a>
<a href="#cl-1525">1525</a>
<a href="#cl-1526">1526</a>
<a href="#cl-1527">1527</a>
<a href="#cl-1528">1528</a>
<a href="#cl-1529">1529</a>
<a href="#cl-1530">1530</a>
<a href="#cl-1531">1531</a>
<a href="#cl-1532">1532</a>
<a href="#cl-1533">1533</a>
<a href="#cl-1534">1534</a>
<a href="#cl-1535">1535</a>
<a href="#cl-1536">1536</a>
<a href="#cl-1537">1537</a>
<a href="#cl-1538">1538</a>
<a href="#cl-1539">1539</a>
<a href="#cl-1540">1540</a>
<a href="#cl-1541">1541</a>
<a href="#cl-1542">1542</a>
<a href="#cl-1543">1543</a>
<a href="#cl-1544">1544</a>
<a href="#cl-1545">1545</a>
<a href="#cl-1546">1546</a>
<a href="#cl-1547">1547</a>
<a href="#cl-1548">1548</a>
<a href="#cl-1549">1549</a>
<a href="#cl-1550">1550</a>
<a href="#cl-1551">1551</a>
<a href="#cl-1552">1552</a>
<a href="#cl-1553">1553</a>
<a href="#cl-1554">1554</a>
<a href="#cl-1555">1555</a>
<a href="#cl-1556">1556</a>
<a href="#cl-1557">1557</a>
<a href="#cl-1558">1558</a>
<a href="#cl-1559">1559</a>
<a href="#cl-1560">1560</a>
<a href="#cl-1561">1561</a>
<a href="#cl-1562">1562</a>
<a href="#cl-1563">1563</a>
<a href="#cl-1564">1564</a>
<a href="#cl-1565">1565</a>
<a href="#cl-1566">1566</a>
<a href="#cl-1567">1567</a>
<a href="#cl-1568">1568</a>
<a href="#cl-1569">1569</a>
<a href="#cl-1570">1570</a>
<a href="#cl-1571">1571</a>
<a href="#cl-1572">1572</a>
<a href="#cl-1573">1573</a>
<a href="#cl-1574">1574</a>
<a href="#cl-1575">1575</a>
<a href="#cl-1576">1576</a>
<a href="#cl-1577">1577</a>
<a href="#cl-1578">1578</a>
<a href="#cl-1579">1579</a>
<a href="#cl-1580">1580</a>
<a href="#cl-1581">1581</a>
<a href="#cl-1582">1582</a>
<a href="#cl-1583">1583</a>
<a href="#cl-1584">1584</a>
<a href="#cl-1585">1585</a>
<a href="#cl-1586">1586</a>
<a href="#cl-1587">1587</a>
<a href="#cl-1588">1588</a>
<a href="#cl-1589">1589</a>
<a href="#cl-1590">1590</a>
<a href="#cl-1591">1591</a>
<a href="#cl-1592">1592</a>
<a href="#cl-1593">1593</a>
<a href="#cl-1594">1594</a>
<a href="#cl-1595">1595</a>
<a href="#cl-1596">1596</a>
<a href="#cl-1597">1597</a>
<a href="#cl-1598">1598</a>
<a href="#cl-1599">1599</a>
<a href="#cl-1600">1600</a>
<a href="#cl-1601">1601</a>
<a href="#cl-1602">1602</a>
<a href="#cl-1603">1603</a>
<a href="#cl-1604">1604</a>
<a href="#cl-1605">1605</a>
<a href="#cl-1606">1606</a>
<a href="#cl-1607">1607</a>
<a href="#cl-1608">1608</a>
<a href="#cl-1609">1609</a>
<a href="#cl-1610">1610</a>
<a href="#cl-1611">1611</a>
<a href="#cl-1612">1612</a>
<a href="#cl-1613">1613</a>
<a href="#cl-1614">1614</a>
<a href="#cl-1615">1615</a>
<a href="#cl-1616">1616</a>
<a href="#cl-1617">1617</a>
<a href="#cl-1618">1618</a>
<a href="#cl-1619">1619</a>
<a href="#cl-1620">1620</a>
<a href="#cl-1621">1621</a>
<a href="#cl-1622">1622</a>
<a href="#cl-1623">1623</a>
<a href="#cl-1624">1624</a>
<a href="#cl-1625">1625</a>
<a href="#cl-1626">1626</a>
<a href="#cl-1627">1627</a>
<a href="#cl-1628">1628</a>
<a href="#cl-1629">1629</a>
</pre></div></td><td class="code"><div class="highlight"><pre><a name="cl-1"></a><span class="cm">/*</span>
<a name="cl-2"></a><span class="cm">Copyright(c) 2011 Nokia Siemens Network</span>
<a name="cl-3"></a><span class="cm">*/</span>
<a name="cl-4"></a><span class="kd">var</span> <span class="nx">C8Y</span> <span class="o">=</span> <span class="nx">C8Y</span> <span class="o">||</span> <span class="p">{};</span>
<a name="cl-5"></a><span class="cm">/**</span>
<a name="cl-6"></a><span class="cm">* @class C8Y.client</span>
<a name="cl-7"></a><span class="cm">* The clientlib class</span>
<a name="cl-8"></a><span class="cm">* @singleton</span>
<a name="cl-9"></a><span class="cm">*/</span>
<a name="cl-10"></a><span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span> <span class="o">=</span> <span class="p">(</span><span class="kd">function</span><span class="p">(){</span>
<a name="cl-11"></a>        <span class="kd">var</span> <span class="nx">that</span> <span class="o">=</span> <span class="k">this</span><span class="p">,</span>
<a name="cl-12"></a>            <span class="nx">output</span><span class="p">,</span>
<a name="cl-13"></a>            <span class="nx">evtbus</span><span class="p">,</span>
<a name="cl-14"></a>            <span class="nx">URL</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">,</span>
<a name="cl-15"></a>            <span class="c1">//URL = &#39;http://dev-l.cumulocity.com:8181&#39;,</span>
<a name="cl-16"></a>            <span class="nx">modelproxies</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-17"></a>            <span class="nx">defaults</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-18"></a>                <span class="nx">headers</span> <span class="o">:</span> <span class="p">{}</span>
<a name="cl-19"></a>            <span class="p">},</span>
<a name="cl-20"></a>            <span class="nx">modules</span> <span class="o">=</span> <span class="p">[];</span>
<a name="cl-21"></a>         
<a name="cl-22"></a>        <span class="k">if</span> <span class="p">(</span><span class="nb">window</span><span class="p">.</span><span class="nx">rooturl</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-23"></a>                <span class="nx">URL</span> <span class="o">=</span> <span class="nb">window</span><span class="p">.</span><span class="nx">rooturl</span><span class="p">;</span>
<a name="cl-24"></a>        <span class="p">}</span>
<a name="cl-25"></a>        <span class="cm">/**</span>
<a name="cl-26"></a><span class="cm">        * </span>
<a name="cl-27"></a><span class="cm">        * Event bus</span>
<a name="cl-28"></a><span class="cm">        * @param {String} the method for encoding</span>
<a name="cl-29"></a><span class="cm">        * @return {String} the new encoded method</span>
<a name="cl-30"></a><span class="cm">        * @method</span>
<a name="cl-31"></a><span class="cm">        */</span>
<a name="cl-32"></a>        <span class="kd">function</span> <span class="nx">init</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-33"></a>            <span class="kd">var</span> <span class="nx">t</span> <span class="o">=</span> <span class="nx">modules</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span>
<a name="cl-34"></a>            
<a name="cl-35"></a>            <span class="nx">output</span><span class="p">.</span><span class="nx">evtbus</span> <span class="o">=</span> <span class="nx">evtbus</span> <span class="o">=</span> <span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-36"></a>            <span class="kd">var</span> <span class="nx">ebus</span> <span class="o">=</span> <span class="nx">Ext</span><span class="p">.</span><span class="nx">create</span><span class="p">(</span><span class="s1">&#39;Ext.util.Observable&#39;</span><span class="p">);</span>
<a name="cl-37"></a>            <span class="nx">ebus</span><span class="p">.</span><span class="nx">addEvents</span><span class="p">(</span><span class="s1">&#39;error&#39;</span><span class="p">);</span>
<a name="cl-38"></a>            <span class="k">return</span> <span class="p">{</span>
<a name="cl-39"></a>                <span class="nx">addListener</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">str</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-40"></a>                    <span class="nx">ebus</span> <span class="o">&amp;&amp;</span> <span class="nx">ebus</span><span class="p">.</span><span class="nx">addListener</span><span class="p">(</span><span class="nx">str</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-41"></a>                <span class="p">},</span>
<a name="cl-42"></a>                <span class="nx">removeListener</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">str</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-43"></a>                    <span class="nx">ebus</span> <span class="o">&amp;&amp;</span> <span class="nx">ebus</span><span class="p">.</span><span class="nx">removeListener</span><span class="p">(</span><span class="nx">str</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-44"></a>                <span class="p">},</span>
<a name="cl-45"></a>                <span class="nx">fireEvent</span> <span class="o">:</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-46"></a>                    <span class="nx">ebus</span> <span class="o">&amp;&amp;</span> <span class="nx">ebus</span><span class="p">.</span><span class="nx">fireEvent</span><span class="p">.</span><span class="nx">apply</span><span class="p">(</span><span class="nx">ebus</span><span class="p">,</span><span class="nx">arguments</span><span class="p">);</span>
<a name="cl-47"></a>                <span class="p">},</span>
<a name="cl-48"></a>                <span class="nx">raw</span> <span class="o">:</span> <span class="nx">ebus</span>
<a name="cl-49"></a>            <span class="p">};</span>
<a name="cl-50"></a>        <span class="p">})();</span>
<a name="cl-51"></a>        
<a name="cl-52"></a>        <span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">t</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-53"></a>            <span class="kd">var</span> <span class="nx">obj</span> <span class="o">=</span> <span class="nx">modules</span><span class="p">[</span><span class="nx">i</span><span class="p">][</span><span class="s1">&#39;obj&#39;</span><span class="p">];</span>
<a name="cl-54"></a>            <span class="nx">_add</span><span class="p">(</span><span class="nx">modules</span><span class="p">[</span><span class="nx">i</span><span class="p">][</span><span class="s1">&#39;name&#39;</span><span class="p">],</span> <span class="nx">obj</span><span class="p">);</span>
<a name="cl-55"></a>            <span class="k">if</span> <span class="p">(</span><span class="nx">obj</span><span class="p">.</span><span class="nx">init</span><span class="p">)</span> <span class="nx">obj</span><span class="p">.</span><span class="nx">init</span><span class="p">();</span>
<a name="cl-56"></a>        <span class="p">}</span>
<a name="cl-57"></a>        <span class="p">}</span>       
<a name="cl-58"></a>    
<a name="cl-59"></a>        
<a name="cl-60"></a>        <span class="cm">/**</span>
<a name="cl-61"></a><span class="cm">        * @private</span>
<a name="cl-62"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-63"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-64"></a><span class="cm">        * @method</span>
<a name="cl-65"></a><span class="cm">        */</span>
<a name="cl-66"></a>        <span class="kd">function</span> <span class="nx">ajax</span><span class="p">(</span><span class="nx">cfg</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-67"></a>            <span class="nx">cfg</span><span class="p">.</span><span class="nx">url</span> <span class="o">=</span> <span class="nx">URL</span> <span class="o">+</span> <span class="nx">cfg</span><span class="p">.</span><span class="nx">url</span><span class="p">;</span>
<a name="cl-68"></a>            
<a name="cl-69"></a>            <span class="k">if</span> <span class="p">(</span><span class="nx">cfg</span><span class="p">.</span><span class="nx">headers</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-70"></a>                <span class="nx">_merge</span><span class="p">(</span><span class="nx">cfg</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span> <span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">);</span>
<a name="cl-71"></a>            <span class="p">}</span> <span class="k">else</span> <span class="p">{</span>
<a name="cl-72"></a>                <span class="nx">cfg</span><span class="p">.</span><span class="nx">headers</span> <span class="o">=</span> <span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">;</span>
<a name="cl-73"></a>            <span class="p">}</span>
<a name="cl-74"></a>            <span class="kd">var</span> <span class="nx">success_callback</span> <span class="o">=</span> <span class="nx">cfg</span><span class="p">.</span><span class="nx">success</span><span class="p">;</span>
<a name="cl-75"></a>            <span class="kd">var</span> <span class="nx">failure_callback</span> <span class="o">=</span> <span class="nx">cfg</span><span class="p">.</span><span class="nx">failure</span><span class="p">;</span>
<a name="cl-76"></a>            <span class="k">delete</span> <span class="nx">cfg</span><span class="p">.</span><span class="nx">success</span><span class="p">;</span>
<a name="cl-77"></a>            <span class="nx">cfg</span><span class="p">.</span><span class="nx">callback</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">options</span><span class="p">,</span> <span class="nx">success</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-78"></a>                <span class="kd">var</span> <span class="nx">message</span><span class="p">,</span><span class="nx">obj</span><span class="p">;</span>
<a name="cl-79"></a>                
<a name="cl-80"></a>                <span class="k">if</span> <span class="p">(</span><span class="nx">response</span><span class="p">.</span><span class="nx">responseText</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-81"></a>                    <span class="k">try</span> <span class="p">{</span>
<a name="cl-82"></a>                    <span class="nx">obj</span> <span class="o">=</span> <span class="nx">_decode</span><span class="p">(</span><span class="nx">response</span><span class="p">.</span><span class="nx">responseText</span><span class="p">);</span>
<a name="cl-83"></a>                    
<a name="cl-84"></a>                <span class="p">}</span> <span class="k">catch</span><span class="p">(</span><span class="nx">e</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-85"></a>                    <span class="nx">success</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>
<a name="cl-86"></a>                    <span class="nx">message</span> <span class="o">=</span> <span class="s1">&#39;Invalid JSON String: &#39;</span><span class="o">+</span> <span class="nx">response</span><span class="p">.</span><span class="nx">responseText</span><span class="p">;</span>
<a name="cl-87"></a>                <span class="p">}</span>
<a name="cl-88"></a>            <span class="p">}</span>
<a name="cl-89"></a>
<a name="cl-90"></a>                <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">success</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-91"></a>                    <span class="k">if</span> <span class="p">(</span><span class="nx">failure_callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-92"></a>                        <span class="nx">failure_callback</span><span class="p">(</span><span class="nx">response</span><span class="p">);</span>
<a name="cl-93"></a>                        <span class="k">return</span><span class="p">;</span>
<a name="cl-94"></a>                    <span class="p">}</span>
<a name="cl-95"></a>                    <span class="nx">evtbus</span><span class="p">.</span><span class="nx">fireEvent</span><span class="p">(</span><span class="s1">&#39;error&#39;</span><span class="p">,</span> <span class="p">{</span>
<a name="cl-96"></a>                        <span class="nx">title</span> <span class="o">:</span> <span class="s1">&#39;Server error&#39;</span><span class="p">,</span>
<a name="cl-97"></a>                        <span class="nx">body</span>  <span class="o">:</span> <span class="nx">message</span> <span class="o">||</span> <span class="nx">obj</span><span class="p">.</span><span class="nx">details</span><span class="p">.</span><span class="nx">exceptionMessage</span>
<a name="cl-98"></a>                    <span class="p">});</span>
<a name="cl-99"></a>                <span class="p">}</span> <span class="k">else</span> <span class="p">{</span>
<a name="cl-100"></a>                    <span class="nx">success_callback</span> <span class="o">&amp;&amp;</span> <span class="nx">success_callback</span><span class="p">(</span><span class="nx">obj</span> <span class="o">||</span> <span class="nx">response</span><span class="p">.</span><span class="nx">responseText</span><span class="p">);</span>
<a name="cl-101"></a>                <span class="p">}</span>
<a name="cl-102"></a>            <span class="p">};</span>
<a name="cl-103"></a>            <span class="nx">cfg</span><span class="p">.</span><span class="nx">disableCaching</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>
<a name="cl-104"></a>            <span class="nx">_ajax</span><span class="p">(</span><span class="nx">cfg</span><span class="p">);</span>
<a name="cl-105"></a>        <span class="p">}</span>
<a name="cl-106"></a>        <span class="cm">/**</span>
<a name="cl-107"></a><span class="cm">        * @private</span>
<a name="cl-108"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-109"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-110"></a><span class="cm">        * @method</span>
<a name="cl-111"></a><span class="cm">        */</span>
<a name="cl-112"></a>        
<a name="cl-113"></a>        <span class="kd">function</span> <span class="nx">_ajax</span><span class="p">(</span><span class="nx">cfg</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-114"></a>        <span class="nx">Ext</span><span class="p">.</span><span class="nx">Ajax</span><span class="p">.</span><span class="nx">request</span><span class="p">(</span><span class="nx">cfg</span><span class="p">);</span>
<a name="cl-115"></a>    <span class="p">}</span>
<a name="cl-116"></a>    <span class="cm">/**</span>
<a name="cl-117"></a><span class="cm">        * @private</span>
<a name="cl-118"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-119"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-120"></a><span class="cm">        * @method</span>
<a name="cl-121"></a><span class="cm">        */</span>
<a name="cl-122"></a>        
<a name="cl-123"></a>    <span class="kd">function</span> <span class="nx">_encode</span><span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-124"></a>        <span class="k">return</span> <span class="nx">Ext</span><span class="p">.</span><span class="nx">encode</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span>
<a name="cl-125"></a>    <span class="p">}</span>
<a name="cl-126"></a>    
<a name="cl-127"></a>        <span class="cm">/**</span>
<a name="cl-128"></a><span class="cm">        * @private</span>
<a name="cl-129"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-130"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-131"></a><span class="cm">        * @method</span>
<a name="cl-132"></a><span class="cm">        */</span>
<a name="cl-133"></a>        
<a name="cl-134"></a>    <span class="kd">function</span> <span class="nx">_decode</span><span class="p">(</span><span class="nx">obj</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-135"></a>        <span class="k">return</span> <span class="nx">Ext</span><span class="p">.</span><span class="nx">decode</span><span class="p">(</span><span class="nx">obj</span><span class="p">);</span>
<a name="cl-136"></a>    <span class="p">}</span>
<a name="cl-137"></a>    
<a name="cl-138"></a>        <span class="cm">/**</span>
<a name="cl-139"></a><span class="cm">        * @private</span>
<a name="cl-140"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-141"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-142"></a><span class="cm">        * @method</span>
<a name="cl-143"></a><span class="cm">        */</span>
<a name="cl-144"></a>
<a name="cl-145"></a>    <span class="kd">function</span> <span class="nx">_merge</span><span class="p">(</span><span class="nx">obj</span><span class="p">,</span><span class="nx">config</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-146"></a>        <span class="nx">Ext</span><span class="p">.</span><span class="nx">applyIf</span><span class="p">(</span><span class="nx">obj</span><span class="p">,</span><span class="nx">config</span><span class="p">);</span>
<a name="cl-147"></a>    <span class="p">}</span>
<a name="cl-148"></a>    
<a name="cl-149"></a>        <span class="cm">/**</span>
<a name="cl-150"></a><span class="cm">        * @private</span>
<a name="cl-151"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-152"></a><span class="cm">        * @param {Object} the request configuration object</span>
<a name="cl-153"></a><span class="cm">        * @method</span>
<a name="cl-154"></a><span class="cm">        */</span>
<a name="cl-155"></a>    <span class="kd">function</span> <span class="nx">_clone</span><span class="p">(</span><span class="nx">obj</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-156"></a>        <span class="k">return</span> <span class="nx">Ext</span><span class="p">.</span><span class="nx">clone</span><span class="p">(</span><span class="nx">obj</span><span class="p">);</span>
<a name="cl-157"></a>    <span class="p">}</span>
<a name="cl-158"></a>        
<a name="cl-159"></a>        <span class="cm">/**</span>
<a name="cl-160"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-161"></a><span class="cm">        * @param {String} the module name</span>
<a name="cl-162"></a><span class="cm">        * @param {Object} the module object to be exposed</span>
<a name="cl-163"></a><span class="cm">        * @method</span>
<a name="cl-164"></a><span class="cm">        */</span>
<a name="cl-165"></a>        <span class="kd">function</span> <span class="nx">add</span><span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">obj</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-166"></a>            <span class="nx">modules</span><span class="p">.</span><span class="nx">push</span><span class="p">({</span>
<a name="cl-167"></a>                <span class="nx">name</span> <span class="o">:</span> <span class="nx">name</span><span class="p">,</span>
<a name="cl-168"></a>                <span class="nx">obj</span>  <span class="o">:</span> <span class="nx">obj</span>
<a name="cl-169"></a>            <span class="p">});</span>
<a name="cl-170"></a>            <span class="nx">obj</span><span class="p">.</span><span class="nx">ajax</span> <span class="o">=</span> <span class="nx">ajax</span><span class="p">;</span>
<a name="cl-171"></a>            <span class="nx">obj</span><span class="p">.</span><span class="nx">defaults</span> <span class="o">=</span> <span class="nx">defaults</span><span class="p">;</span>
<a name="cl-172"></a>            <span class="nx">obj</span><span class="p">.</span><span class="nx">output</span> <span class="o">=</span> <span class="nx">output</span><span class="p">;</span>
<a name="cl-173"></a>            <span class="nx">obj</span><span class="p">.</span><span class="nx">buildUrl</span> <span class="o">=</span> <span class="nx">buildUrl</span><span class="p">;</span>
<a name="cl-174"></a>            <span class="nx">output</span><span class="p">[</span><span class="nx">name</span><span class="p">]</span> <span class="o">=</span> <span class="nx">obj</span><span class="p">;</span>
<a name="cl-175"></a>        <span class="p">}</span>
<a name="cl-176"></a>        
<a name="cl-177"></a>        <span class="kd">function</span> <span class="nx">_add</span><span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">obj</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-178"></a>            <span class="nx">obj</span><span class="p">.</span><span class="nx">evtbus</span> <span class="o">=</span> <span class="nx">evtbus</span><span class="p">;</span>
<a name="cl-179"></a>        <span class="p">}</span>
<a name="cl-180"></a>        
<a name="cl-181"></a>        <span class="cm">/**</span>
<a name="cl-182"></a><span class="cm">        * Makes a regular AJAX call</span>
<a name="cl-183"></a><span class="cm">        * @param {Object} the map to be added to the client in order to be server as needed</span>
<a name="cl-184"></a><span class="cm">        * @method</span>
<a name="cl-185"></a><span class="cm">        */</span>
<a name="cl-186"></a>        <span class="kd">function</span> <span class="nx">addModelProxy</span><span class="p">(</span><span class="nx">proxy</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-187"></a>            <span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">k</span> <span class="k">in</span> <span class="nx">proxy</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-188"></a>                <span class="kd">var</span> <span class="nx">p</span> <span class="o">=</span> <span class="nx">proxy</span><span class="p">[</span><span class="nx">k</span><span class="p">];</span>
<a name="cl-189"></a>                <span class="k">if</span> <span class="p">(</span><span class="nx">p</span><span class="p">.</span><span class="nx">url</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-190"></a>                <span class="nx">p</span><span class="p">.</span><span class="nx">url</span> <span class="o">=</span> <span class="nx">URL</span> <span class="o">+</span> <span class="nx">p</span><span class="p">.</span><span class="nx">url</span><span class="p">;</span>
<a name="cl-191"></a>            <span class="p">}</span>
<a name="cl-192"></a>            <span class="k">if</span> <span class="p">(</span><span class="nx">p</span><span class="p">.</span><span class="nx">headers</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-193"></a>                <span class="nx">_merge</span><span class="p">(</span><span class="nx">p</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span> <span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">);</span>
<a name="cl-194"></a>            <span class="p">}</span> <span class="k">else</span> <span class="p">{</span>
<a name="cl-195"></a>                <span class="nx">p</span><span class="p">.</span><span class="nx">headers</span> <span class="o">=</span> <span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">;</span>
<a name="cl-196"></a>            <span class="p">}</span>
<a name="cl-197"></a>            <span class="p">}</span>
<a name="cl-198"></a>            <span class="nx">_merge</span><span class="p">(</span><span class="nx">modelproxies</span><span class="p">,</span> <span class="nx">proxy</span><span class="p">);</span>
<a name="cl-199"></a>        <span class="p">}</span>
<a name="cl-200"></a>        
<a name="cl-201"></a>        <span class="cm">/**</span>
<a name="cl-202"></a><span class="cm">        * Returns the proxy added by each module</span>
<a name="cl-203"></a><span class="cm">        * @param {String} the proxy key defined by each module</span>
<a name="cl-204"></a><span class="cm">        * @method</span>
<a name="cl-205"></a><span class="cm">        */</span>
<a name="cl-206"></a>        <span class="kd">function</span> <span class="nx">getModelProxy</span><span class="p">(</span><span class="nx">key</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-207"></a>            <span class="kd">var</span> <span class="nx">proxy</span> <span class="o">=</span> <span class="nx">modelproxies</span><span class="p">[</span><span class="nx">key</span><span class="p">],</span>
<a name="cl-208"></a>                <span class="nx">result</span> <span class="o">=</span> <span class="nx">proxy</span> <span class="o">?</span> <span class="nx">proxy</span> <span class="o">:</span> <span class="kc">null</span><span class="p">;</span>
<a name="cl-209"></a>            <span class="k">return</span> <span class="nx">result</span><span class="p">;</span>
<a name="cl-210"></a>        <span class="p">}</span>
<a name="cl-211"></a>
<a name="cl-212"></a>        <span class="cm">/**</span>
<a name="cl-213"></a><span class="cm">        * Returns the complete</span>
<a name="cl-214"></a><span class="cm">        * @param {String} URL path to be completed with the domain</span>
<a name="cl-215"></a><span class="cm">        * @returns {String} url, the complete url with the domain and path name</span>
<a name="cl-216"></a><span class="cm">        * @method</span>
<a name="cl-217"></a><span class="cm">        */</span>
<a name="cl-218"></a>        <span class="kd">function</span> <span class="nx">buildUrl</span><span class="p">(</span><span class="nx">path</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-219"></a>                <span class="k">return</span> <span class="nx">URL</span> <span class="o">+</span> <span class="nx">path</span><span class="p">;</span>
<a name="cl-220"></a>        <span class="p">}</span>
<a name="cl-221"></a>        
<a name="cl-222"></a>        <span class="nx">output</span> <span class="o">=</span>  <span class="p">{</span>
<a name="cl-223"></a>            <span class="nx">init</span>            <span class="o">:</span> <span class="nx">init</span><span class="p">,</span>
<a name="cl-224"></a>            <span class="nx">add</span>             <span class="o">:</span> <span class="nx">add</span><span class="p">,</span>
<a name="cl-225"></a>            <span class="nx">addModelProxy</span>   <span class="o">:</span> <span class="nx">addModelProxy</span><span class="p">,</span>
<a name="cl-226"></a>            <span class="nx">getModelProxy</span>   <span class="o">:</span> <span class="nx">getModelProxy</span><span class="p">,</span>
<a name="cl-227"></a>            <span class="nx">buildUrl</span>            <span class="o">:</span> <span class="nx">buildUrl</span>
<a name="cl-228"></a>        <span class="p">};</span>
<a name="cl-229"></a>        
<a name="cl-230"></a>        <span class="nx">Ext</span><span class="p">.</span><span class="nx">require</span><span class="p">([</span><span class="s1">&#39;Ext.util.Observable&#39;</span><span class="p">]);</span>
<a name="cl-231"></a>        <span class="nx">Ext</span><span class="p">.</span><span class="nx">onReady</span><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-232"></a>                <span class="nx">output</span><span class="p">.</span><span class="nx">init</span><span class="p">();</span>
<a name="cl-233"></a>        <span class="p">});</span>
<a name="cl-234"></a>                
<a name="cl-235"></a>        <span class="k">return</span> <span class="nx">output</span><span class="p">;</span>
<a name="cl-236"></a><span class="p">})();</span>
<a name="cl-237"></a><span class="cm">/**</span>
<a name="cl-238"></a><span class="cm">* @class C8Y.client.auth</span>
<a name="cl-239"></a><span class="cm">* Auth Class, mimics cumulocity REST API Auth methods</span>
<a name="cl-240"></a><span class="cm">* @singleton</span>
<a name="cl-241"></a><span class="cm">*/</span>
<a name="cl-242"></a><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-243"></a>    <span class="kd">var</span> <span class="nx">o</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-244"></a>        <span class="nx">props</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-245"></a>    
<a name="cl-246"></a>    <span class="kd">var</span> <span class="nx">Base64</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-247"></a>        <span class="c1">// private property</span>
<a name="cl-248"></a>        <span class="nx">_keyStr</span> <span class="o">:</span> <span class="s2">&quot;ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=&quot;</span><span class="p">,</span>
<a name="cl-249"></a>                <span class="cm">/**</span>
<a name="cl-250"></a><span class="cm">                * @private</span>
<a name="cl-251"></a><span class="cm">                * Return an encoded Base64 Object</span>
<a name="cl-252"></a><span class="cm">                * @param {String} the method for encoding</span>
<a name="cl-253"></a><span class="cm">                * @return {String} the new encoded method</span>
<a name="cl-254"></a><span class="cm">                * @method</span>
<a name="cl-255"></a><span class="cm">                */</span>
<a name="cl-256"></a>        <span class="nx">encode</span> <span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">input</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-257"></a>                <span class="kd">var</span> <span class="nx">output</span> <span class="o">=</span> <span class="s2">&quot;&quot;</span><span class="p">;</span>
<a name="cl-258"></a>                <span class="kd">var</span> <span class="nx">chr1</span><span class="p">,</span> <span class="nx">chr2</span><span class="p">,</span> <span class="nx">chr3</span><span class="p">,</span> <span class="nx">enc1</span><span class="p">,</span> <span class="nx">enc2</span><span class="p">,</span> <span class="nx">enc3</span><span class="p">,</span> <span class="nx">enc4</span><span class="p">;</span>
<a name="cl-259"></a>                <span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>
<a name="cl-260"></a>
<a name="cl-261"></a>                <span class="nx">input</span> <span class="o">=</span> <span class="nx">Base64</span><span class="p">.</span><span class="nx">_utf8_encode</span><span class="p">(</span><span class="nx">input</span><span class="p">);</span>
<a name="cl-262"></a>
<a name="cl-263"></a>                <span class="k">while</span> <span class="p">(</span><span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">input</span><span class="p">.</span><span class="nx">length</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-264"></a>
<a name="cl-265"></a>                        <span class="nx">chr1</span> <span class="o">=</span> <span class="nx">input</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">);</span>
<a name="cl-266"></a>                        <span class="nx">chr2</span> <span class="o">=</span> <span class="nx">input</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">);</span>
<a name="cl-267"></a>                        <span class="nx">chr3</span> <span class="o">=</span> <span class="nx">input</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">);</span>
<a name="cl-268"></a>
<a name="cl-269"></a>                        <span class="nx">enc1</span> <span class="o">=</span> <span class="nx">chr1</span> <span class="o">&gt;&gt;</span> <span class="mi">2</span><span class="p">;</span>
<a name="cl-270"></a>                        <span class="nx">enc2</span> <span class="o">=</span> <span class="p">((</span><span class="nx">chr1</span> <span class="o">&amp;</span> <span class="mi">3</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">4</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">chr2</span> <span class="o">&gt;&gt;</span> <span class="mi">4</span><span class="p">);</span>
<a name="cl-271"></a>                        <span class="nx">enc3</span> <span class="o">=</span> <span class="p">((</span><span class="nx">chr2</span> <span class="o">&amp;</span> <span class="mi">15</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">2</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">chr3</span> <span class="o">&gt;&gt;</span> <span class="mi">6</span><span class="p">);</span>
<a name="cl-272"></a>                        <span class="nx">enc4</span> <span class="o">=</span> <span class="nx">chr3</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">;</span>
<a name="cl-273"></a>
<a name="cl-274"></a>                        <span class="k">if</span> <span class="p">(</span><span class="nb">isNaN</span><span class="p">(</span><span class="nx">chr2</span><span class="p">))</span> <span class="p">{</span>
<a name="cl-275"></a>                                <span class="nx">enc3</span> <span class="o">=</span> <span class="nx">enc4</span> <span class="o">=</span> <span class="mi">64</span><span class="p">;</span>
<a name="cl-276"></a>                        <span class="p">}</span> <span class="k">else</span> <span class="k">if</span> <span class="p">(</span><span class="nb">isNaN</span><span class="p">(</span><span class="nx">chr3</span><span class="p">))</span> <span class="p">{</span>
<a name="cl-277"></a>                                <span class="nx">enc4</span> <span class="o">=</span> <span class="mi">64</span><span class="p">;</span>
<a name="cl-278"></a>                        <span class="p">}</span>
<a name="cl-279"></a>
<a name="cl-280"></a>                        <span class="nx">output</span> <span class="o">=</span> <span class="nx">output</span> <span class="o">+</span>
<a name="cl-281"></a>                        <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">enc1</span><span class="p">)</span> <span class="o">+</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">enc2</span><span class="p">)</span> <span class="o">+</span>
<a name="cl-282"></a>                        <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">enc3</span><span class="p">)</span> <span class="o">+</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">enc4</span><span class="p">);</span>
<a name="cl-283"></a>
<a name="cl-284"></a>                <span class="p">}</span>
<a name="cl-285"></a>
<a name="cl-286"></a>                <span class="k">return</span> <span class="nx">output</span><span class="p">;</span>
<a name="cl-287"></a>        <span class="p">},</span>
<a name="cl-288"></a>
<a name="cl-289"></a>                <span class="cm">/**</span>
<a name="cl-290"></a><span class="cm">                * @private</span>
<a name="cl-291"></a><span class="cm">                * Return a decoded Base64 Object</span>
<a name="cl-292"></a><span class="cm">                * @param {String} the method for encoding</span>
<a name="cl-293"></a><span class="cm">                * @return {String} the new encoded method</span>
<a name="cl-294"></a><span class="cm">                * @method</span>
<a name="cl-295"></a><span class="cm">                */</span>
<a name="cl-296"></a>                
<a name="cl-297"></a>        <span class="nx">decode</span> <span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">input</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-298"></a>                <span class="kd">var</span> <span class="nx">output</span> <span class="o">=</span> <span class="s2">&quot;&quot;</span><span class="p">;</span>
<a name="cl-299"></a>                <span class="kd">var</span> <span class="nx">chr1</span><span class="p">,</span> <span class="nx">chr2</span><span class="p">,</span> <span class="nx">chr3</span><span class="p">;</span>
<a name="cl-300"></a>                <span class="kd">var</span> <span class="nx">enc1</span><span class="p">,</span> <span class="nx">enc2</span><span class="p">,</span> <span class="nx">enc3</span><span class="p">,</span> <span class="nx">enc4</span><span class="p">;</span>
<a name="cl-301"></a>                <span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>
<a name="cl-302"></a>
<a name="cl-303"></a>                <span class="nx">input</span> <span class="o">=</span> <span class="nx">input</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="sr">/[^A-Za-z0-9\+\/\=]/g</span><span class="p">,</span> <span class="s2">&quot;&quot;</span><span class="p">);</span>
<a name="cl-304"></a>
<a name="cl-305"></a>                <span class="k">while</span> <span class="p">(</span><span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">input</span><span class="p">.</span><span class="nx">length</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-306"></a>
<a name="cl-307"></a>                        <span class="nx">enc1</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">input</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">));</span>
<a name="cl-308"></a>                        <span class="nx">enc2</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">input</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">));</span>
<a name="cl-309"></a>                        <span class="nx">enc3</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">input</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">));</span>
<a name="cl-310"></a>                        <span class="nx">enc4</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">_keyStr</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">input</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="o">++</span><span class="p">));</span>
<a name="cl-311"></a>
<a name="cl-312"></a>                        <span class="nx">chr1</span> <span class="o">=</span> <span class="p">(</span><span class="nx">enc1</span> <span class="o">&lt;&lt;</span> <span class="mi">2</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">enc2</span> <span class="o">&gt;&gt;</span> <span class="mi">4</span><span class="p">);</span>
<a name="cl-313"></a>                        <span class="nx">chr2</span> <span class="o">=</span> <span class="p">((</span><span class="nx">enc2</span> <span class="o">&amp;</span> <span class="mi">15</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">4</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">enc3</span> <span class="o">&gt;&gt;</span> <span class="mi">2</span><span class="p">);</span>
<a name="cl-314"></a>                        <span class="nx">chr3</span> <span class="o">=</span> <span class="p">((</span><span class="nx">enc3</span> <span class="o">&amp;</span> <span class="mi">3</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">6</span><span class="p">)</span> <span class="o">|</span> <span class="nx">enc4</span><span class="p">;</span>
<a name="cl-315"></a>
<a name="cl-316"></a>                        <span class="nx">output</span> <span class="o">=</span> <span class="nx">output</span> <span class="o">+</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nx">chr1</span><span class="p">);</span>
<a name="cl-317"></a>
<a name="cl-318"></a>                        <span class="k">if</span> <span class="p">(</span><span class="nx">enc3</span> <span class="o">!=</span> <span class="mi">64</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-319"></a>                                <span class="nx">output</span> <span class="o">=</span> <span class="nx">output</span> <span class="o">+</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nx">chr2</span><span class="p">);</span>
<a name="cl-320"></a>                        <span class="p">}</span>
<a name="cl-321"></a>                        <span class="k">if</span> <span class="p">(</span><span class="nx">enc4</span> <span class="o">!=</span> <span class="mi">64</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-322"></a>                                <span class="nx">output</span> <span class="o">=</span> <span class="nx">output</span> <span class="o">+</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nx">chr3</span><span class="p">);</span>
<a name="cl-323"></a>                        <span class="p">}</span>
<a name="cl-324"></a>
<a name="cl-325"></a>                <span class="p">}</span>
<a name="cl-326"></a>
<a name="cl-327"></a>                <span class="nx">output</span> <span class="o">=</span> <span class="nx">Base64</span><span class="p">.</span><span class="nx">_utf8_decode</span><span class="p">(</span><span class="nx">output</span><span class="p">);</span>
<a name="cl-328"></a>
<a name="cl-329"></a>                <span class="k">return</span> <span class="nx">output</span><span class="p">;</span>
<a name="cl-330"></a>
<a name="cl-331"></a>        <span class="p">},</span>
<a name="cl-332"></a>
<a name="cl-333"></a>        <span class="cm">/**</span>
<a name="cl-334"></a><span class="cm">                * @private</span>
<a name="cl-335"></a><span class="cm">                * Return an encoded UTF-8 Object</span>
<a name="cl-336"></a><span class="cm">                * @param {String} the method for encoding</span>
<a name="cl-337"></a><span class="cm">                * @return {String} the new encoded method</span>
<a name="cl-338"></a><span class="cm">                * @method</span>
<a name="cl-339"></a><span class="cm">                */</span>
<a name="cl-340"></a>                
<a name="cl-341"></a>        <span class="nx">_utf8_encode</span> <span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">string</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-342"></a>                <span class="nx">string</span> <span class="o">=</span> <span class="nx">string</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="sr">/\r\n/g</span><span class="p">,</span><span class="s2">&quot;\n&quot;</span><span class="p">);</span>
<a name="cl-343"></a>                <span class="kd">var</span> <span class="nx">utftext</span> <span class="o">=</span> <span class="s2">&quot;&quot;</span><span class="p">;</span>
<a name="cl-344"></a>
<a name="cl-345"></a>                <span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">n</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">n</span> <span class="o">&lt;</span> <span class="nx">string</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">n</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-346"></a>
<a name="cl-347"></a>                        <span class="kd">var</span> <span class="nx">c</span> <span class="o">=</span> <span class="nx">string</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">n</span><span class="p">);</span>
<a name="cl-348"></a>
<a name="cl-349"></a>                        <span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">&lt;</span> <span class="mi">128</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-350"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nx">c</span><span class="p">);</span>
<a name="cl-351"></a>                        <span class="p">}</span>
<a name="cl-352"></a>                        <span class="k">else</span> <span class="k">if</span><span class="p">((</span><span class="nx">c</span> <span class="o">&gt;</span> <span class="mi">127</span><span class="p">)</span> <span class="o">&amp;&amp;</span> <span class="p">(</span><span class="nx">c</span> <span class="o">&lt;</span> <span class="mi">2048</span><span class="p">))</span> <span class="p">{</span>
<a name="cl-353"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">((</span><span class="nx">c</span> <span class="o">&gt;&gt;</span> <span class="mi">6</span><span class="p">)</span> <span class="o">|</span> <span class="mi">192</span><span class="p">);</span>
<a name="cl-354"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">((</span><span class="nx">c</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">)</span> <span class="o">|</span> <span class="mi">128</span><span class="p">);</span>
<a name="cl-355"></a>                        <span class="p">}</span>
<a name="cl-356"></a>                        <span class="k">else</span> <span class="p">{</span>
<a name="cl-357"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">((</span><span class="nx">c</span> <span class="o">&gt;&gt;</span> <span class="mi">12</span><span class="p">)</span> <span class="o">|</span> <span class="mi">224</span><span class="p">);</span>
<a name="cl-358"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(((</span><span class="nx">c</span> <span class="o">&gt;&gt;</span> <span class="mi">6</span><span class="p">)</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">)</span> <span class="o">|</span> <span class="mi">128</span><span class="p">);</span>
<a name="cl-359"></a>                                <span class="nx">utftext</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">((</span><span class="nx">c</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">)</span> <span class="o">|</span> <span class="mi">128</span><span class="p">);</span>
<a name="cl-360"></a>                        <span class="p">}</span>
<a name="cl-361"></a>
<a name="cl-362"></a>                <span class="p">}</span>
<a name="cl-363"></a>
<a name="cl-364"></a>                <span class="k">return</span> <span class="nx">utftext</span><span class="p">;</span>
<a name="cl-365"></a>        <span class="p">},</span>
<a name="cl-366"></a>
<a name="cl-367"></a>        <span class="cm">/**</span>
<a name="cl-368"></a><span class="cm">                * @private</span>
<a name="cl-369"></a><span class="cm">                * Return a decoded UTF-8 Object</span>
<a name="cl-370"></a><span class="cm">                * @param {String} the method for encoding</span>
<a name="cl-371"></a><span class="cm">                * @return {String} the new encoded method</span>
<a name="cl-372"></a><span class="cm">                * @method</span>
<a name="cl-373"></a><span class="cm">                */</span>
<a name="cl-374"></a>                
<a name="cl-375"></a>        <span class="nx">_utf8_decode</span> <span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">utftext</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-376"></a>                <span class="kd">var</span> <span class="nx">string</span> <span class="o">=</span> <span class="s2">&quot;&quot;</span><span class="p">;</span>
<a name="cl-377"></a>                <span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>
<a name="cl-378"></a>                <span class="kd">var</span> <span class="nx">c</span> <span class="o">=</span> <span class="nx">c1</span> <span class="o">=</span> <span class="nx">c2</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>
<a name="cl-379"></a>
<a name="cl-380"></a>                <span class="k">while</span> <span class="p">(</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">utftext</span><span class="p">.</span><span class="nx">length</span> <span class="p">)</span> <span class="p">{</span>
<a name="cl-381"></a>
<a name="cl-382"></a>                        <span class="nx">c</span> <span class="o">=</span> <span class="nx">utftext</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="p">);</span>
<a name="cl-383"></a>
<a name="cl-384"></a>                        <span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">&lt;</span> <span class="mi">128</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-385"></a>                                <span class="nx">string</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nx">c</span><span class="p">);</span>
<a name="cl-386"></a>                                <span class="nx">i</span><span class="o">++</span><span class="p">;</span>
<a name="cl-387"></a>                        <span class="p">}</span>
<a name="cl-388"></a>                        <span class="k">else</span> <span class="k">if</span><span class="p">((</span><span class="nx">c</span> <span class="o">&gt;</span> <span class="mi">191</span><span class="p">)</span> <span class="o">&amp;&amp;</span> <span class="p">(</span><span class="nx">c</span> <span class="o">&lt;</span> <span class="mi">224</span><span class="p">))</span> <span class="p">{</span>
<a name="cl-389"></a>                                <span class="nx">c2</span> <span class="o">=</span> <span class="nx">utftext</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">+</span><span class="mi">1</span><span class="p">);</span>
<a name="cl-390"></a>                                <span class="nx">string</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(((</span><span class="nx">c</span> <span class="o">&amp;</span> <span class="mi">31</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">6</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">c2</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">));</span>
<a name="cl-391"></a>                                <span class="nx">i</span> <span class="o">+=</span> <span class="mi">2</span><span class="p">;</span>
<a name="cl-392"></a>                        <span class="p">}</span>
<a name="cl-393"></a>                        <span class="k">else</span> <span class="p">{</span>
<a name="cl-394"></a>                                <span class="nx">c2</span> <span class="o">=</span> <span class="nx">utftext</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">+</span><span class="mi">1</span><span class="p">);</span>
<a name="cl-395"></a>                                <span class="nx">c3</span> <span class="o">=</span> <span class="nx">utftext</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="o">+</span><span class="mi">2</span><span class="p">);</span>
<a name="cl-396"></a>                                <span class="nx">string</span> <span class="o">+=</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(((</span><span class="nx">c</span> <span class="o">&amp;</span> <span class="mi">15</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">12</span><span class="p">)</span> <span class="o">|</span> <span class="p">((</span><span class="nx">c2</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">)</span> <span class="o">&lt;&lt;</span> <span class="mi">6</span><span class="p">)</span> <span class="o">|</span> <span class="p">(</span><span class="nx">c3</span> <span class="o">&amp;</span> <span class="mi">63</span><span class="p">));</span>
<a name="cl-397"></a>                                <span class="nx">i</span> <span class="o">+=</span> <span class="mi">3</span><span class="p">;</span>
<a name="cl-398"></a>                        <span class="p">}</span>
<a name="cl-399"></a>
<a name="cl-400"></a>                <span class="p">}</span>
<a name="cl-401"></a>
<a name="cl-402"></a>                <span class="k">return</span> <span class="nx">string</span><span class="p">;</span>
<a name="cl-403"></a>        <span class="p">}</span>
<a name="cl-404"></a>
<a name="cl-405"></a>    <span class="p">};</span>
<a name="cl-406"></a>        <span class="cm">/**</span>
<a name="cl-407"></a><span class="cm">         * @private</span>
<a name="cl-408"></a><span class="cm">         * Return an encoded Base64 Object</span>
<a name="cl-409"></a><span class="cm">         * @param {String} the method for encoding</span>
<a name="cl-410"></a><span class="cm">         * @return {String} the new encoded method</span>
<a name="cl-411"></a><span class="cm">         * @method</span>
<a name="cl-412"></a><span class="cm">         */</span>
<a name="cl-413"></a>    <span class="kd">function</span> <span class="nx">makeAuthHeader</span><span class="p">(</span><span class="nx">t</span><span class="p">,</span><span class="nx">u</span><span class="p">,</span><span class="nx">p</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-414"></a>        <span class="kd">var</span> <span class="nx">str</span> <span class="o">=</span> <span class="nx">t</span><span class="o">+</span><span class="s2">&quot;/&quot;</span><span class="o">+</span><span class="nx">u</span><span class="o">+</span><span class="s2">&quot;:&quot;</span><span class="o">+</span><span class="nx">p</span><span class="p">;</span>
<a name="cl-415"></a>        <span class="k">return</span> <span class="s1">&#39;Basic &#39;</span> <span class="o">+</span> <span class="nx">Base64</span><span class="p">.</span><span class="nx">encode</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span>
<a name="cl-416"></a>    <span class="p">};</span>
<a name="cl-417"></a>
<a name="cl-418"></a>        <span class="cm">/**</span>
<a name="cl-419"></a><span class="cm">         * Does the login procedure, although this is publicly available the application handles the authentication process, the developer</span>
<a name="cl-420"></a><span class="cm">         * shouldn&#39;t have to call this method directly</span>
<a name="cl-421"></a><span class="cm">         * @param {String} tenant the platform tenant</span>
<a name="cl-422"></a><span class="cm">         * @param {String} username the platform username</span>
<a name="cl-423"></a><span class="cm">         * @param {String} password the platform password</span>
<a name="cl-424"></a><span class="cm">         * @param {Function} callback the login callback</span>
<a name="cl-425"></a><span class="cm">         * @returns {Object} ajax the AJAX Object</span>
<a name="cl-426"></a><span class="cm">         * @method</span>
<a name="cl-427"></a><span class="cm">         *</span>
<a name="cl-428"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-429"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-430"></a><span class="cm">                C8Y.client.auth.login(&quot;tenant&quot;, &quot;username&quot;, &quot;password&quot;, function(r){</span>
<a name="cl-431"></a><span class="cm">                ..</span>
<a name="cl-432"></a><span class="cm">                //Do something here</span>
<a name="cl-433"></a><span class="cm">                });</span>
<a name="cl-434"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-435"></a><span class="cm">         *</span>
<a name="cl-436"></a><span class="cm">     */</span>
<a name="cl-437"></a>     <span class="nx">o</span><span class="p">.</span><span class="nx">login</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">_tenant</span><span class="p">,</span> <span class="nx">_username</span><span class="p">,</span> <span class="nx">_password</span><span class="p">,</span> <span class="nx">callback</span><span class="p">,</span> <span class="nx">callback_failure</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-438"></a>         <span class="nx">props</span><span class="p">.</span><span class="nx">tenant</span> <span class="o">=</span> <span class="nx">_tenant</span><span class="p">;</span>
<a name="cl-439"></a>         <span class="nx">props</span><span class="p">.</span><span class="nx">username</span> <span class="o">=</span> <span class="nx">_username</span><span class="p">;</span>
<a name="cl-440"></a>         <span class="nx">props</span><span class="p">.</span><span class="nx">authheader</span> <span class="o">=</span> <span class="nx">makeAuthHeader</span><span class="p">(</span><span class="nx">_tenant</span><span class="p">,</span> <span class="nx">_username</span><span class="p">,</span> <span class="nx">_password</span><span class="p">);</span>
<a name="cl-441"></a>
<a name="cl-442"></a>         <span class="kd">var</span> <span class="nx">headers</span> <span class="o">=</span> <span class="nx">props</span><span class="p">.</span><span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span> <span class="s1">&#39;Authorization&#39;</span> <span class="o">:</span> <span class="nx">props</span><span class="p">.</span><span class="nx">authheader</span> <span class="p">};</span>
<a name="cl-443"></a>         <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-444"></a>             <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-445"></a>             <span class="nx">url</span>     <span class="o">:</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="o">+</span><span class="nx">_username</span><span class="p">,</span>
<a name="cl-446"></a>             <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-447"></a>                 <span class="nx">props</span><span class="p">.</span><span class="nx">user</span> <span class="o">=</span> <span class="nx">r</span><span class="p">;</span>
<a name="cl-448"></a>                 <span class="nx">o</span><span class="p">.</span><span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">[</span><span class="s1">&#39;Authorization&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">props</span><span class="p">.</span><span class="nx">authheader</span><span class="p">;</span>
<a name="cl-449"></a>                 <span class="nx">o</span><span class="p">.</span><span class="nx">evtbus</span><span class="p">.</span><span class="nx">fireEvent</span><span class="p">(</span><span class="s1">&#39;login&#39;</span><span class="p">,</span> <span class="nx">props</span><span class="p">.</span><span class="nx">user</span><span class="p">);</span>
<a name="cl-450"></a>                 <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-451"></a>             <span class="p">},</span>
<a name="cl-452"></a>             <span class="nx">failure</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-453"></a>                <span class="nx">o</span><span class="p">.</span><span class="nx">evtbus</span><span class="p">.</span><span class="nx">fireEvent</span><span class="p">(</span><span class="s1">&#39;loginfailed&#39;</span><span class="p">);</span>
<a name="cl-454"></a>                                <span class="nx">callback_failure</span> <span class="o">&amp;&amp;</span> <span class="nx">callback_failure</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-455"></a>             <span class="p">}</span>
<a name="cl-456"></a>         <span class="p">});</span>
<a name="cl-457"></a>     <span class="p">};</span>
<a name="cl-458"></a>    
<a name="cl-459"></a>        <span class="cm">/**</span>
<a name="cl-460"></a><span class="cm">         * Does the logout procedure, although this is publicly available the application handles the authentication process, the developer</span>
<a name="cl-461"></a><span class="cm">         * shouldn&#39;t have to call this method directly</span>
<a name="cl-462"></a><span class="cm">         * @method</span>
<a name="cl-463"></a><span class="cm">         *</span>
<a name="cl-464"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-465"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-466"></a><span class="cm">                C8Y.client.auth.logout();</span>
<a name="cl-467"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-468"></a><span class="cm">         *</span>
<a name="cl-469"></a><span class="cm">     */</span>
<a name="cl-470"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">logout</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-471"></a>        <span class="nx">user</span> <span class="o">=</span> <span class="kc">undefined</span><span class="p">;</span>
<a name="cl-472"></a>        <span class="nx">authheader</span> <span class="o">=</span> <span class="kc">undefined</span><span class="p">;</span>
<a name="cl-473"></a>        <span class="nx">tenant</span> <span class="o">=</span> <span class="kc">undefined</span><span class="p">;</span>
<a name="cl-474"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">evtbus</span><span class="p">.</span><span class="nx">fireEvent</span><span class="p">(</span><span class="s1">&#39;logout&#39;</span><span class="p">);</span>
<a name="cl-475"></a>    <span class="p">};</span>
<a name="cl-476"></a>    <span class="cm">/**</span>
<a name="cl-477"></a><span class="cm">         * Does the getUser procedure, returning an object with the user information</span>
<a name="cl-478"></a><span class="cm">         * @returns {Object} user the User Object</span>
<a name="cl-479"></a><span class="cm">         * @method</span>
<a name="cl-480"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-481"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-482"></a><span class="cm">                C8Y.client.auth.getUser();</span>
<a name="cl-483"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-484"></a><span class="cm">         *</span>
<a name="cl-485"></a><span class="cm">     */</span>
<a name="cl-486"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">getUser</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-487"></a>        <span class="k">return</span> <span class="nx">props</span><span class="p">.</span><span class="nx">user</span><span class="p">;</span>
<a name="cl-488"></a>    <span class="p">};</span>
<a name="cl-489"></a>    <span class="cm">/**</span>
<a name="cl-490"></a><span class="cm">         * Does the getTenant procedure, returning an object with the tenant information</span>
<a name="cl-491"></a><span class="cm">         * @returns {Object} tenant the Tenant Object</span>
<a name="cl-492"></a><span class="cm">         * @method</span>
<a name="cl-493"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-494"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-495"></a><span class="cm">                C8Y.client.auth.getTenant();</span>
<a name="cl-496"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-497"></a><span class="cm">         *</span>
<a name="cl-498"></a><span class="cm">     */</span>
<a name="cl-499"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">getTenant</span>  <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-500"></a>        <span class="k">return</span> <span class="nx">props</span><span class="p">.</span><span class="nx">tenant</span><span class="p">;</span>
<a name="cl-501"></a>    <span class="p">};</span>
<a name="cl-502"></a>    
<a name="cl-503"></a>        <span class="cm">/**</span>
<a name="cl-504"></a><span class="cm">         * Does the getAuthHeader procedure, returning an object with the autheader information</span>
<a name="cl-505"></a><span class="cm">         * @returns {Object} authheader the AuthenticationHeader Object</span>
<a name="cl-506"></a><span class="cm">         * @method</span>
<a name="cl-507"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-508"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-509"></a><span class="cm">                C8Y.client.auth.getAuthHeader();</span>
<a name="cl-510"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-511"></a><span class="cm">         *</span>
<a name="cl-512"></a><span class="cm">     */</span>
<a name="cl-513"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">getAuthHeader</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-514"></a>        <span class="k">return</span> <span class="nx">props</span><span class="p">.</span><span class="nx">authheader</span><span class="p">;</span>
<a name="cl-515"></a>    <span class="p">};</span>    
<a name="cl-516"></a>    
<a name="cl-517"></a>        <span class="cm">/**</span>
<a name="cl-518"></a><span class="cm">         * Does the isLoggedin procedure, returning a Boolean with the user status</span>
<a name="cl-519"></a><span class="cm">         * @returns {Boolean} user the User Status</span>
<a name="cl-520"></a><span class="cm">         * @method</span>
<a name="cl-521"></a><span class="cm">         * &lt;p&gt;For example:&lt;/p&gt;</span>
<a name="cl-522"></a><span class="cm">                &lt;pre&gt;&lt;code&gt;</span>
<a name="cl-523"></a><span class="cm">                C8Y.client.auth.isLoggedin();</span>
<a name="cl-524"></a><span class="cm">                &lt;/code&gt;&lt;/pre&gt;</span>
<a name="cl-525"></a><span class="cm">         *</span>
<a name="cl-526"></a><span class="cm">     */</span>
<a name="cl-527"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">isLoggedin</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-528"></a>        <span class="k">return</span> <span class="o">!!</span><span class="nx">props</span><span class="p">.</span><span class="nx">user</span><span class="p">;</span>
<a name="cl-529"></a>    <span class="p">};</span>
<a name="cl-530"></a>    
<a name="cl-531"></a>    <span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s1">&#39;auth&#39;</span><span class="p">,</span> <span class="nx">o</span><span class="p">);</span>
<a name="cl-532"></a>    
<a name="cl-533"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">init</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-534"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getUser</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">getUser</span><span class="p">;</span>
<a name="cl-535"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">;</span>
<a name="cl-536"></a>    <span class="p">}</span>
<a name="cl-537"></a><span class="p">})();</span>
<a name="cl-538"></a><span class="cm">/**</span>
<a name="cl-539"></a><span class="cm"> * @class C8Y.client.inventory</span>
<a name="cl-540"></a><span class="cm"> * Inventory class, this class mimics the REST Interface&#39;s methods</span>
<a name="cl-541"></a><span class="cm"> * @singleton</span>
<a name="cl-542"></a><span class="cm"> */</span>
<a name="cl-543"></a><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-544"></a>    <span class="kd">var</span> <span class="nx">o</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-545"></a>        <span class="nx">props</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-546"></a>        <span class="nx">proxymodels</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-547"></a>        
<a name="cl-548"></a>        <span class="cm">/**</span>
<a name="cl-549"></a><span class="cm">         * Return a tenant Object</span>
<a name="cl-550"></a><span class="cm">         * @return {String} tenant, the tenant object</span>
<a name="cl-551"></a><span class="cm">         * @method</span>
<a name="cl-552"></a><span class="cm">         */</span>    
<a name="cl-553"></a>    <span class="kd">function</span> <span class="nx">getTenant</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-554"></a>        <span class="kd">var</span> <span class="nx">tenant</span><span class="p">;</span>
<a name="cl-555"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span> <span class="o">||</span> <span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-556"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;getTenant() not defined, please check the auth module.&#39;</span><span class="p">);</span>
<a name="cl-557"></a>        <span class="p">}</span>
<a name="cl-558"></a>        <span class="k">return</span>  <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">();</span>
<a name="cl-559"></a>    <span class="p">}</span>
<a name="cl-560"></a>    
<a name="cl-561"></a>   <span class="cm">/**</span>
<a name="cl-562"></a><span class="cm">        * Returns a collection of Managed Objects</span>
<a name="cl-563"></a><span class="cm">        * @param {String} typeFilter, the string for the Managed Object type to list</span>
<a name="cl-564"></a><span class="cm">        * @param {int} pageSize, the number of items per page</span>
<a name="cl-565"></a><span class="cm">        * @param {int} currentPage, the current page to display</span>
<a name="cl-566"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-567"></a><span class="cm">        * @return {Object} ajax, the ajax response object with a collection of managed objects.</span>
<a name="cl-568"></a><span class="cm">        * @method</span>
<a name="cl-569"></a><span class="cm">        */</span>    
<a name="cl-570"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">list</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">typeFilter</span><span class="p">,</span> <span class="nx">pageSize</span><span class="p">,</span> <span class="nx">currentPage</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-571"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-572"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects&#39;</span><span class="p">,</span>
<a name="cl-573"></a>                        <span class="nx">params</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-574"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-575"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObjectCollection+json;ver=0.9&#39;</span>
<a name="cl-576"></a>            <span class="p">};</span>
<a name="cl-577"></a>                
<a name="cl-578"></a>                <span class="k">if</span> <span class="p">(</span><span class="nx">typeFilter</span><span class="p">)</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;type&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">typeFilter</span><span class="p">;</span>
<a name="cl-579"></a>                <span class="k">if</span> <span class="p">(</span><span class="nx">pageSize</span><span class="p">)</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;pageSize&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">pageSize</span><span class="p">;</span>
<a name="cl-580"></a>                <span class="k">if</span> <span class="p">(</span><span class="nx">currentPage</span><span class="p">)</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;currentPage&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">currentPage</span><span class="p">;</span>
<a name="cl-581"></a>
<a name="cl-582"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-583"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-584"></a>            <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-585"></a>                        <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-586"></a>                        <span class="nx">params</span>  <span class="o">:</span> <span class="nx">params</span><span class="p">,</span>
<a name="cl-587"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-588"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-589"></a>            <span class="p">}</span>
<a name="cl-590"></a>        <span class="p">});</span>
<a name="cl-591"></a>    <span class="p">};</span>
<a name="cl-592"></a>    
<a name="cl-593"></a>   <span class="cm">/**</span>
<a name="cl-594"></a><span class="cm">        * Return the created managed object</span>
<a name="cl-595"></a><span class="cm">        * @param {Object} data, the managed object to be created</span>
<a name="cl-596"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-597"></a><span class="cm">        * @return {Object} ajax, the ajax response with the managed object itself</span>
<a name="cl-598"></a><span class="cm">        * @method</span>
<a name="cl-599"></a><span class="cm">        */</span>
<a name="cl-600"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">create</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-601"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-602"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects&#39;</span><span class="p">,</span>
<a name="cl-603"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-604"></a>                <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObject+json;ver=0.9&#39;</span>
<a name="cl-605"></a>            <span class="p">};</span>
<a name="cl-606"></a>        
<a name="cl-607"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-608"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-609"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-610"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-611"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-612"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-613"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-614"></a>            <span class="p">}</span>
<a name="cl-615"></a>        <span class="p">});</span>
<a name="cl-616"></a>    <span class="p">};</span>
<a name="cl-617"></a>
<a name="cl-618"></a>   <span class="cm">/**</span>
<a name="cl-619"></a><span class="cm">        * Returns the managed object for the passed identifier</span>
<a name="cl-620"></a><span class="cm">        * @param {String} id, the managed object identifier</span>
<a name="cl-621"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-622"></a><span class="cm">        * @return {Object} ajax the ajax response with the managed object for the passed id</span>
<a name="cl-623"></a><span class="cm">        * @method</span>
<a name="cl-624"></a><span class="cm">        */</span>    
<a name="cl-625"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">get</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-626"></a>        
<a name="cl-627"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-628"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-629"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-630"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObject+json;ver=0.9&#39;</span>
<a name="cl-631"></a>            <span class="p">};</span>
<a name="cl-632"></a>        
<a name="cl-633"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-634"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-635"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-636"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-637"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-638"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-639"></a>            <span class="p">}</span>
<a name="cl-640"></a>        <span class="p">});</span>
<a name="cl-641"></a>    <span class="p">};</span>
<a name="cl-642"></a>
<a name="cl-643"></a>   <span class="cm">/**</span>
<a name="cl-644"></a><span class="cm">        * Return the updated managed object</span>
<a name="cl-645"></a><span class="cm">        * @param {String} id, the identifier of the managed object</span>
<a name="cl-646"></a><span class="cm">        * @param {Object} data, the managed object updated data</span>
<a name="cl-647"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-648"></a><span class="cm">        * @return {Object} ajax the ajax response with the managed object itself</span>
<a name="cl-649"></a><span class="cm">        * @method</span>
<a name="cl-650"></a><span class="cm">        */</span>    
<a name="cl-651"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">update</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-652"></a>        
<a name="cl-653"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-654"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-655"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-656"></a>                <span class="s1">&#39;Content-Type&#39;</span>  <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObject+json;ver=0.9&#39;</span><span class="p">,</span>
<a name="cl-657"></a>                <span class="s1">&#39;Accept&#39;</span>                <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObject+json;ver=0.9&#39;</span>
<a name="cl-658"></a>            <span class="p">};</span>
<a name="cl-659"></a>        
<a name="cl-660"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-661"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-662"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-663"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;PUT&#39;</span><span class="p">,</span>
<a name="cl-664"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-665"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-666"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-667"></a>            <span class="p">}</span>
<a name="cl-668"></a>        <span class="p">});</span>
<a name="cl-669"></a>    <span class="p">};</span>
<a name="cl-670"></a>    
<a name="cl-671"></a>   <span class="cm">/**</span>
<a name="cl-672"></a><span class="cm">        * Returns 204 deleted reference</span>
<a name="cl-673"></a><span class="cm">        * @param {String} id, the identifier of the managed object</span>
<a name="cl-674"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-675"></a><span class="cm">        * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-676"></a><span class="cm">        * @method</span>
<a name="cl-677"></a><span class="cm">        */</span>    
<a name="cl-678"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">remove</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-679"></a>        
<a name="cl-680"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-681"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">;</span>
<a name="cl-682"></a>        
<a name="cl-683"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-684"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-685"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-686"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-687"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-688"></a>            <span class="p">}</span>
<a name="cl-689"></a>        <span class="p">});</span>
<a name="cl-690"></a>    <span class="p">};</span>
<a name="cl-691"></a>    
<a name="cl-692"></a>   <span class="cm">/**</span>
<a name="cl-693"></a><span class="cm">        * Returns the child device collection for the passed parent identifier</span>
<a name="cl-694"></a><span class="cm">        * @param {String} parentid, the parent identifier</span>
<a name="cl-695"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-696"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device collection</span>
<a name="cl-697"></a><span class="cm">        * @method</span>
<a name="cl-698"></a><span class="cm">        */</span>    
<a name="cl-699"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">listDevices</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parentid</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-700"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-701"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">parentid</span><span class="o">+</span><span class="s1">&#39;/childDevices&#39;</span><span class="p">,</span>
<a name="cl-702"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-703"></a>                <span class="c1">//Although this is the proper accept header we will allways get a 500 error with any Accept header</span>
<a name="cl-704"></a>                <span class="c1">// &#39;Accept&#39; : &#39;application/vnd.com.nsn.cumulocity.managedObjectCollectionRepresentation+json;ver=0.9&#39;</span>
<a name="cl-705"></a>            <span class="p">};</span>
<a name="cl-706"></a>        
<a name="cl-707"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-708"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-709"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-710"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-711"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-712"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-713"></a>            <span class="p">}</span>
<a name="cl-714"></a>        <span class="p">});</span>
<a name="cl-715"></a>    <span class="p">};</span>
<a name="cl-716"></a>    
<a name="cl-717"></a>    <span class="cm">/**</span>
<a name="cl-718"></a><span class="cm">        * Adds child devices to a specific Managed Object</span>
<a name="cl-719"></a><span class="cm">        * @param {String} parent, the parent identifier</span>
<a name="cl-720"></a><span class="cm">        * @param {Object} child, the child object casted as a Managed Object</span>
<a name="cl-721"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-722"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device itself</span>
<a name="cl-723"></a><span class="cm">        * @method</span>
<a name="cl-724"></a><span class="cm">        */</span>    
<a name="cl-725"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">addDevice</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parent</span><span class="p">,</span> <span class="nx">child</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-726"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">parent</span><span class="p">.</span><span class="nx">id</span><span class="o">+</span><span class="s1">&#39;/childDevices&#39;</span><span class="p">,</span>
<a name="cl-727"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-728"></a>                <span class="s1">&#39;Content-Type&#39;</span><span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObjectReference+json;ver=0.9&#39;</span>
<a name="cl-729"></a>            <span class="p">};</span>
<a name="cl-730"></a>        
<a name="cl-731"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-732"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-733"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-734"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="p">{</span><span class="s1">&#39;managedObject&#39;</span><span class="o">:</span> <span class="nx">child</span><span class="p">},</span>
<a name="cl-735"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-736"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-737"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-738"></a>            <span class="p">}</span>
<a name="cl-739"></a>        <span class="p">});</span>
<a name="cl-740"></a>    <span class="p">};</span>
<a name="cl-741"></a>
<a name="cl-742"></a>   <span class="cm">/**</span>
<a name="cl-743"></a><span class="cm">        * Removes the Child Device from the specified device</span>
<a name="cl-744"></a><span class="cm">        * @param {String} child, the child identifier</span>
<a name="cl-745"></a><span class="cm">        * @param {String} parent, the parent identifier</span>
<a name="cl-746"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-747"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device reference and response status code in the HTTP 1.1 Format</span>
<a name="cl-748"></a><span class="cm">        * @method</span>
<a name="cl-749"></a><span class="cm">        */</span>    
<a name="cl-750"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">removeDevice</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parent</span><span class="p">,</span> <span class="nx">child</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-751"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="p">(</span><span class="nx">parent</span><span class="p">.</span><span class="nx">id</span> <span class="o">||</span> <span class="nx">parent</span><span class="p">)</span><span class="o">+</span><span class="s1">&#39;/childDevices/&#39;</span><span class="o">+</span><span class="p">(</span><span class="nx">child</span><span class="p">.</span><span class="nx">id</span> <span class="o">||</span> <span class="nx">child</span><span class="p">);</span>
<a name="cl-752"></a>        
<a name="cl-753"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-754"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-755"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-756"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-757"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-758"></a>            <span class="p">}</span>
<a name="cl-759"></a>        <span class="p">});</span>
<a name="cl-760"></a>    <span class="p">};</span>
<a name="cl-761"></a>
<a name="cl-762"></a>        <span class="cm">/**</span>
<a name="cl-763"></a><span class="cm">        * Returns the child assets collection for the passed parent identifier</span>
<a name="cl-764"></a><span class="cm">        * @param {String} parentid, the parent identifier</span>
<a name="cl-765"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-766"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device collection</span>
<a name="cl-767"></a><span class="cm">        * @method</span>
<a name="cl-768"></a><span class="cm">        */</span> 
<a name="cl-769"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">listAssets</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parentid</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-770"></a>            <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-771"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">parentid</span><span class="o">+</span><span class="s1">&#39;/childAssets&#39;</span><span class="p">,</span>
<a name="cl-772"></a>                <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-773"></a>                    <span class="c1">//Although this is the proper accept header we will allways get a 500 error with any Accept header</span>
<a name="cl-774"></a>                    <span class="c1">// &#39;Accept&#39; : &#39;application/vnd.com.nsn.cumulocity.managedObjectCollectionRepresentation+json;ver=0.9&#39;</span>
<a name="cl-775"></a>                <span class="p">};</span>
<a name="cl-776"></a>
<a name="cl-777"></a>            <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-778"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-779"></a>                <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-780"></a>                <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-781"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-782"></a>                    <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-783"></a>                <span class="p">}</span>
<a name="cl-784"></a>            <span class="p">});</span>
<a name="cl-785"></a>        <span class="p">};</span>
<a name="cl-786"></a>
<a name="cl-787"></a>        <span class="cm">/**</span>
<a name="cl-788"></a><span class="cm">        * Add a child asset</span>
<a name="cl-789"></a><span class="cm">        * @param {String} parent, the parent identifier</span>
<a name="cl-790"></a><span class="cm">        * @param {Object} child, the child object casted as a Managed Object</span>
<a name="cl-791"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-792"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device itself</span>
<a name="cl-793"></a><span class="cm">        * @method</span>
<a name="cl-794"></a><span class="cm">        */</span> 
<a name="cl-795"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">addAsset</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parent</span><span class="p">,</span> <span class="nx">child</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-796"></a>            <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-797"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">parent</span><span class="p">.</span><span class="nx">id</span><span class="o">+</span><span class="s1">&#39;/childAssets&#39;</span><span class="p">,</span>
<a name="cl-798"></a>                <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-799"></a>                    <span class="s1">&#39;Content-Type&#39;</span><span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.managedObjectReference+json;ver=0.9&#39;</span>
<a name="cl-800"></a>                    <span class="c1">//Although this is the proper accept header we will allways get a 500 error with any Accept header</span>
<a name="cl-801"></a>                    <span class="c1">// &#39;Accept&#39; : &#39;application/vnd.com.nsn.cumulocity.managedObjectCollectionRepresentation+json;ver=0.9&#39;</span>
<a name="cl-802"></a>                    <span class="c1">// &#39;Accept&#39; : &#39;&#39;</span>
<a name="cl-803"></a>                <span class="p">};</span>
<a name="cl-804"></a>
<a name="cl-805"></a>            <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-806"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-807"></a>                <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-808"></a>                <span class="nx">jsonData</span><span class="o">:</span> <span class="p">{</span><span class="s1">&#39;managedObject&#39;</span><span class="o">:</span> <span class="nx">child</span><span class="p">},</span>
<a name="cl-809"></a>                <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-810"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-811"></a>                    <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-812"></a>                <span class="p">}</span>
<a name="cl-813"></a>            <span class="p">});</span>
<a name="cl-814"></a>        <span class="p">};</span>
<a name="cl-815"></a>
<a name="cl-816"></a>        <span class="cm">/**</span>
<a name="cl-817"></a><span class="cm">        * Deletes the child asset reference and a status code</span>
<a name="cl-818"></a><span class="cm">        * @param {String} child, the child identifier</span>
<a name="cl-819"></a><span class="cm">        * @param {String} parent, the parent identifier</span>
<a name="cl-820"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-821"></a><span class="cm">        * @return {Object} ajax, the ajax response with the child device reference and response status code in the HTTP 1.1 Format</span>
<a name="cl-822"></a><span class="cm">        * @method</span>
<a name="cl-823"></a><span class="cm">        */</span> 
<a name="cl-824"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">removeAsset</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">parent</span><span class="p">,</span> <span class="nx">child</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-825"></a>            <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="p">(</span><span class="nx">parent</span><span class="p">.</span><span class="nx">id</span> <span class="o">||</span> <span class="nx">parent</span><span class="p">)</span><span class="o">+</span><span class="s1">&#39;/childAssets/&#39;</span><span class="o">+</span><span class="p">(</span><span class="nx">child</span><span class="p">.</span><span class="nx">id</span> <span class="o">||</span> <span class="nx">child</span><span class="p">);</span>
<a name="cl-826"></a>
<a name="cl-827"></a>            <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-828"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-829"></a>                <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-830"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-831"></a>                    <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-832"></a>                <span class="p">}</span>
<a name="cl-833"></a>            <span class="p">});</span>
<a name="cl-834"></a>        <span class="p">};</span>
<a name="cl-835"></a>        
<a name="cl-836"></a>        <span class="cm">/**</span>
<a name="cl-837"></a><span class="cm">        * Returns the proxy object for Managed Object Models</span>
<a name="cl-838"></a><span class="cm">        * @param {String} type, the Managed Object type to filter on</span>
<a name="cl-839"></a><span class="cm">        * @method</span>
<a name="cl-840"></a><span class="cm">        */</span>
<a name="cl-841"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getProxy</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">typ</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-842"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span> <span class="o">+</span> <span class="p">(</span><span class="nx">typ</span> <span class="o">?</span> <span class="s2">&quot;?type=&quot;</span><span class="o">+</span><span class="nx">typ</span> <span class="o">:</span> <span class="s1">&#39;&#39;</span><span class="p">),</span>
<a name="cl-843"></a>                        <span class="nx">config</span><span class="p">;</span>
<a name="cl-844"></a>                
<a name="cl-845"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">buildUrl</span><span class="p">(</span><span class="nx">url</span><span class="p">);</span>
<a name="cl-846"></a>                <span class="nx">config</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-847"></a>                        <span class="nx">type</span>        <span class="o">:</span> <span class="s1">&#39;rest&#39;</span><span class="p">,</span>
<a name="cl-848"></a>                        <span class="nx">url</span>         <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-849"></a>                        <span class="nx">headers</span>         <span class="o">:</span> <span class="nx">o</span><span class="p">.</span><span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-850"></a>                        <span class="nx">pageParam</span>   <span class="o">:</span> <span class="s1">&#39;currentPage&#39;</span><span class="p">,</span>
<a name="cl-851"></a>                        <span class="nx">limitParam</span>  <span class="o">:</span> <span class="s1">&#39;pageSize&#39;</span><span class="p">,</span>
<a name="cl-852"></a>                        <span class="nx">startParam</span>  <span class="o">:</span> <span class="kc">null</span><span class="p">,</span>
<a name="cl-853"></a>                        <span class="nx">reader</span>      <span class="o">:</span> <span class="p">{</span>
<a name="cl-854"></a>                            <span class="nx">type</span>    <span class="o">:</span> <span class="s1">&#39;json&#39;</span><span class="p">,</span>
<a name="cl-855"></a>                            <span class="nx">root</span>    <span class="o">:</span> <span class="s1">&#39;managedObjects&#39;</span><span class="p">,</span>
<a name="cl-856"></a>                            <span class="nx">totalProperty</span> <span class="o">:</span> <span class="s1">&#39;total&#39;</span><span class="p">,</span>
<a name="cl-857"></a>                            <span class="nx">getResponseData</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">response</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-858"></a>                                <span class="kd">var</span> <span class="nx">data</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">self</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">getResponseData</span><span class="p">(</span><span class="nx">response</span><span class="p">);</span>
<a name="cl-859"></a>                                <span class="k">if</span> <span class="p">(</span><span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-860"></a>                                    <span class="nx">data</span><span class="p">[</span><span class="s1">&#39;total&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">pageSize</span> <span class="o">*</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">totalPages</span><span class="p">;</span>
<a name="cl-861"></a>                                <span class="p">}</span>
<a name="cl-862"></a>                                <span class="k">return</span> <span class="nx">data</span><span class="p">;</span>
<a name="cl-863"></a>                            <span class="p">}</span>
<a name="cl-864"></a>                        <span class="p">}</span>
<a name="cl-865"></a>                <span class="p">};</span>
<a name="cl-866"></a>
<a name="cl-867"></a>                <span class="k">return</span> <span class="nx">config</span><span class="p">;</span>
<a name="cl-868"></a>        <span class="p">};</span>
<a name="cl-869"></a>        
<a name="cl-870"></a>   
<a name="cl-871"></a>    <span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s1">&#39;inventory&#39;</span><span class="p">,</span> <span class="nx">o</span><span class="p">);</span>
<a name="cl-872"></a><span class="p">})();</span>
<a name="cl-873"></a><span class="p">(</span><span class="kd">function</span><span class="p">(){</span>
<a name="cl-874"></a>        
<a name="cl-875"></a>        <span class="kd">var</span> <span class="nx">o</span> <span class="o">=</span> <span class="p">{},</span> 
<a name="cl-876"></a>                <span class="nx">props</span> <span class="o">=</span> <span class="p">{},</span> 
<a name="cl-877"></a>                <span class="nx">proxymodels</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-878"></a>        
<a name="cl-879"></a>        <span class="kd">function</span> <span class="nx">getUser</span><span class="p">(){</span>
<a name="cl-880"></a>                
<a name="cl-881"></a>                <span class="kd">var</span> <span class="nx">user</span><span class="p">;</span>
<a name="cl-882"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span> <span class="o">||</span> <span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getUser</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-883"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;getUser() not defined, please check the auth module.&#39;</span><span class="p">);</span>
<a name="cl-884"></a>        <span class="p">}</span>
<a name="cl-885"></a>        <span class="k">return</span>  <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getUser</span><span class="p">();</span>
<a name="cl-886"></a>                
<a name="cl-887"></a>        <span class="p">}</span>
<a name="cl-888"></a>        
<a name="cl-889"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">list</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">callback</span><span class="p">){</span>
<a name="cl-890"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-891"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="p">,</span>
<a name="cl-892"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-893"></a>            <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.cumulocity.userCollection+json;ver=0.9&#39;</span>
<a name="cl-894"></a>        <span class="p">};</span>
<a name="cl-895"></a>    
<a name="cl-896"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-897"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-898"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-899"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-900"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-901"></a>                <span class="p">}</span>
<a name="cl-902"></a>        <span class="p">});</span>
<a name="cl-903"></a>        <span class="p">};</span>
<a name="cl-904"></a>        
<a name="cl-905"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">get</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-906"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-907"></a>                    <span class="nx">tenant</span> <span class="o">=</span> 
<a name="cl-908"></a>                    <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-909"></a>                    <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-910"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.user+json;ver=0.9&#39;</span>
<a name="cl-911"></a>            <span class="p">};</span>
<a name="cl-912"></a>                
<a name="cl-913"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-914"></a>                        <span class="nx">url</span>     <span class="o">:</span><span class="nx">url</span><span class="p">,</span>
<a name="cl-915"></a>                        <span class="nx">headers</span> <span class="o">:</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-916"></a>                        <span class="nx">method</span>  <span class="o">:</span><span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-917"></a>                        <span class="nx">success</span> <span class="o">:</span><span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">){</span><span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);}</span>
<a name="cl-918"></a>                <span class="p">});</span>
<a name="cl-919"></a>        <span class="p">};</span>
<a name="cl-920"></a>        
<a name="cl-921"></a>        
<a name="cl-922"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getByName</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-923"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-924"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/userByName/&#39;</span><span class="o">+</span><span class="nx">name</span><span class="p">,</span>
<a name="cl-925"></a>                <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-926"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.user+json;ver=0.9&#39;</span>
<a name="cl-927"></a>            <span class="p">};</span>
<a name="cl-928"></a>                
<a name="cl-929"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-930"></a>                        <span class="nx">url</span>     <span class="o">:</span><span class="nx">url</span><span class="p">,</span>
<a name="cl-931"></a>                        <span class="nx">headers</span> <span class="o">:</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-932"></a>                        <span class="nx">method</span>  <span class="o">:</span><span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-933"></a>                        <span class="nx">success</span> <span class="o">:</span><span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">){</span><span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);}</span>
<a name="cl-934"></a>                <span class="p">});</span>
<a name="cl-935"></a>        <span class="p">};</span>
<a name="cl-936"></a>        
<a name="cl-937"></a>        
<a name="cl-938"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">create</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-939"></a>                
<a name="cl-940"></a>                 <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-941"></a>         <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="p">,</span>
<a name="cl-942"></a>         <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-943"></a>             <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.user+json;ver=0.9&#39;</span>
<a name="cl-944"></a>         <span class="p">};</span>
<a name="cl-945"></a>
<a name="cl-946"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-947"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-948"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-949"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-950"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-951"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-952"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-953"></a>            <span class="p">}</span>
<a name="cl-954"></a>        <span class="p">});</span>
<a name="cl-955"></a>        <span class="p">}</span>
<a name="cl-956"></a>        
<a name="cl-957"></a>     <span class="nx">o</span><span class="p">.</span><span class="nx">update</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-958"></a>        <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-959"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-960"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-961"></a>                        <span class="s1">&#39;Content-Type&#39;</span>  <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.user+json;ver=0.9&#39;</span><span class="p">,</span>
<a name="cl-962"></a>                <span class="s1">&#39;Accept&#39;</span>                <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.user+json;ver=0.9&#39;</span>
<a name="cl-963"></a>            <span class="p">};</span>
<a name="cl-964"></a>    
<a name="cl-965"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-966"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-967"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-968"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;PUT&#39;</span><span class="p">,</span>
<a name="cl-969"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-970"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-971"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-972"></a>            <span class="p">}</span>
<a name="cl-973"></a>        <span class="p">});</span>
<a name="cl-974"></a>    <span class="p">};</span>
<a name="cl-975"></a>        
<a name="cl-976"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">remove</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-977"></a>        
<a name="cl-978"></a>        <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">user</span><span class="p">(),</span>
<a name="cl-979"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">;</span>
<a name="cl-980"></a>        
<a name="cl-981"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-982"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-983"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-984"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-985"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-986"></a>            <span class="p">}</span>
<a name="cl-987"></a>        <span class="p">});</span>
<a name="cl-988"></a>    <span class="p">};</span>
<a name="cl-989"></a>            
<a name="cl-990"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">listGroups</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">callback</span><span class="p">){</span>
<a name="cl-991"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-992"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="p">,</span>
<a name="cl-993"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-994"></a>            <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.cumulocity.groupCollection+json;ver=0.9&#39;</span>
<a name="cl-995"></a>        <span class="p">};</span>
<a name="cl-996"></a>    
<a name="cl-997"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-998"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-999"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1000"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1001"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1002"></a>                <span class="p">}</span>
<a name="cl-1003"></a>        <span class="p">});</span>
<a name="cl-1004"></a>        <span class="p">};</span>
<a name="cl-1005"></a>    
<a name="cl-1006"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">createGroup</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1007"></a>                
<a name="cl-1008"></a>                 <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-1009"></a>         <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="p">,</span>
<a name="cl-1010"></a>         <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1011"></a>             <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.group+json;ver=0.9&#39;</span>
<a name="cl-1012"></a>         <span class="p">};</span>
<a name="cl-1013"></a>                        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1014"></a>                    <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1015"></a>                    <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-1016"></a>                    <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1017"></a>                    <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-1018"></a>                    <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1019"></a>                        <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1020"></a>                    <span class="p">}</span>
<a name="cl-1021"></a>                <span class="p">});</span>
<a name="cl-1022"></a>        <span class="p">}</span>
<a name="cl-1023"></a>    
<a name="cl-1024"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1025"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-1026"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1027"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1028"></a>            <span class="c1">// &#39;Accept&#39; : &#39;application/vnd.com.cumulocity.group+json;ver=0.9&#39;</span>
<a name="cl-1029"></a>        <span class="p">};</span>
<a name="cl-1030"></a>    
<a name="cl-1031"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1032"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1033"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1034"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1035"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1036"></a>                <span class="p">}</span>
<a name="cl-1037"></a>        <span class="p">});</span>
<a name="cl-1038"></a>        <span class="p">};</span>
<a name="cl-1039"></a>    
<a name="cl-1040"></a>        
<a name="cl-1041"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getGroupByName</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1042"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-1043"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groupByName/&#39;</span><span class="o">+</span><span class="nx">name</span><span class="p">,</span>
<a name="cl-1044"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1045"></a>            <span class="c1">//Although this is the proper accept header we will allways get a 500 error with any Accept header</span>
<a name="cl-1046"></a>            <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.cumulocity.group+json;ver=0.9&#39;</span>
<a name="cl-1047"></a>        <span class="p">};</span>
<a name="cl-1048"></a>    
<a name="cl-1049"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1050"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1051"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1052"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1053"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1054"></a>                <span class="p">}</span>
<a name="cl-1055"></a>        <span class="p">});</span>
<a name="cl-1056"></a>        <span class="p">};</span>
<a name="cl-1057"></a>        
<a name="cl-1058"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">removeGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1059"></a>        
<a name="cl-1060"></a>        <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">user</span><span class="p">(),</span>
<a name="cl-1061"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1062"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1063"></a>                <span class="c1">//Although this is the proper accept header we will allways get a 500 error with any Accept header</span>
<a name="cl-1064"></a>                <span class="c1">// &#39;Accept&#39; : &#39;application/vnd.com.nsn.cumulocity.managedObjectCollectionRepresentation+json;ver=0.9&#39;</span>
<a name="cl-1065"></a>                <span class="c1">// &#39;Accept&#39; : &#39;&#39;</span>
<a name="cl-1066"></a>            <span class="p">};</span>
<a name="cl-1067"></a>        
<a name="cl-1068"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1069"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1070"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-1071"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1072"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1073"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1074"></a>            <span class="p">}</span>
<a name="cl-1075"></a>        <span class="p">});</span>
<a name="cl-1076"></a>    <span class="p">};</span>
<a name="cl-1077"></a>        
<a name="cl-1078"></a>    
<a name="cl-1079"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">updateGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1080"></a>        <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-1081"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1082"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1083"></a>                    <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.group+json;ver=0.9&#39;</span><span class="p">,</span>
<a name="cl-1084"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.group+json;ver=0.9&#39;</span>
<a name="cl-1085"></a>            <span class="p">};</span>
<a name="cl-1086"></a>    
<a name="cl-1087"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1088"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1089"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-1090"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;PUT&#39;</span><span class="p">,</span>
<a name="cl-1091"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1092"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1093"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1094"></a>            <span class="p">}</span>
<a name="cl-1095"></a>        <span class="p">});</span>
<a name="cl-1096"></a>    <span class="p">};</span>
<a name="cl-1097"></a>        
<a name="cl-1098"></a>    
<a name="cl-1099"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">addToGroup</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1100"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-1101"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="o">+</span><span class="s1">&#39;/users&#39;</span><span class="p">,</span>
<a name="cl-1102"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1103"></a>                <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.userReference+json;ver=0.9&#39;</span>
<a name="cl-1104"></a>            <span class="p">};</span>
<a name="cl-1105"></a>            
<a name="cl-1106"></a>                        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1107"></a>                    <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1108"></a>                    <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-1109"></a>                    <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1110"></a>                    <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-1111"></a>                    <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1112"></a>                        <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1113"></a>                    <span class="p">}</span>
<a name="cl-1114"></a>                <span class="p">});</span>
<a name="cl-1115"></a>        <span class="p">};</span>
<a name="cl-1116"></a>    
<a name="cl-1117"></a>    
<a name="cl-1118"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">removeFromGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">group</span> <span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1119"></a>        <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span>
<a name="cl-1120"></a>                <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">group</span><span class="o">+</span><span class="s1">&#39;/users/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">;</span>
<a name="cl-1121"></a>        
<a name="cl-1122"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1123"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1124"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-1125"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1126"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1127"></a>            <span class="p">}</span>
<a name="cl-1128"></a>        <span class="p">});</span>
<a name="cl-1129"></a>    <span class="p">};</span>
<a name="cl-1130"></a>    
<a name="cl-1131"></a>    
<a name="cl-1132"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">getInGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">group</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1133"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-1134"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span><span class="o">+</span><span class="nx">group</span><span class="o">+</span><span class="s1">&#39;/users&#39;</span><span class="p">,</span>
<a name="cl-1135"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1136"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.cumulocity.userReferenceCollection+json;ver=0.9&#39;</span>
<a name="cl-1137"></a>            <span class="p">};</span>
<a name="cl-1138"></a>    
<a name="cl-1139"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1140"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1141"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1142"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1143"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1144"></a>                <span class="p">}</span>
<a name="cl-1145"></a>        <span class="p">});</span>
<a name="cl-1146"></a>        <span class="p">};</span>
<a name="cl-1147"></a>    
<a name="cl-1148"></a>    
<a name="cl-1149"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getUserGroups</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1150"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-1151"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="o">+</span><span class="s1">&#39;/groups&#39;</span><span class="p">,</span>
<a name="cl-1152"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1153"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.groupreferencecollection+json&#39;</span>
<a name="cl-1154"></a>            <span class="p">};</span>
<a name="cl-1155"></a>    
<a name="cl-1156"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1157"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1158"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1159"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1160"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1161"></a>                <span class="p">}</span>
<a name="cl-1162"></a>        <span class="p">});</span>
<a name="cl-1163"></a>        <span class="p">};</span>
<a name="cl-1164"></a>                
<a name="cl-1165"></a>                
<a name="cl-1166"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">listRoles</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">callback</span><span class="p">){</span>
<a name="cl-1167"></a>                <span class="kd">var</span> <span class="nx">user</span> <span class="o">=</span> <span class="nx">getUser</span><span class="p">(),</span> 
<a name="cl-1168"></a>                        <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/roles/&#39;</span><span class="p">,</span>
<a name="cl-1169"></a>                        <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1170"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.roleCollection+json;ver=0.9&#39;</span>
<a name="cl-1171"></a>            <span class="p">};</span>
<a name="cl-1172"></a>
<a name="cl-1173"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1174"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1175"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1176"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1177"></a>                    <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1178"></a>                <span class="p">}</span>
<a name="cl-1179"></a>        <span class="p">});</span>
<a name="cl-1180"></a>        <span class="p">};</span>
<a name="cl-1181"></a>
<a name="cl-1182"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">addRoleToUser</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">userid</span><span class="p">,</span> <span class="nx">roledata</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1183"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span> <span class="o">+</span> <span class="nx">userid</span> <span class="o">+</span> <span class="s1">&#39;/roles/&#39;</span><span class="p">,</span>
<a name="cl-1184"></a>                    <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1185"></a>                            <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.roleReference+json;ver=0.9&#39;</span><span class="p">,</span>
<a name="cl-1186"></a>                            <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.roleReference+json;ver=0.9&#39;</span>
<a name="cl-1187"></a>                    <span class="p">};</span>
<a name="cl-1188"></a>        
<a name="cl-1189"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1190"></a>                <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1191"></a>                        <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-1192"></a>                        <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">roledata</span><span class="p">,</span>
<a name="cl-1193"></a>                <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1194"></a>                <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1195"></a>                        <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1196"></a>                <span class="p">}</span>
<a name="cl-1197"></a>        <span class="p">});</span>
<a name="cl-1198"></a>        <span class="p">};</span>
<a name="cl-1199"></a>
<a name="cl-1200"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">addRoleToGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">groupid</span><span class="p">,</span> <span class="nx">roledata</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1201"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span> <span class="o">+</span> <span class="nx">groupid</span> <span class="o">+</span> <span class="s1">&#39;/roles/&#39;</span><span class="p">,</span>
<a name="cl-1202"></a>                    <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1203"></a>                            <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.roleReference+json;ver=0.9&#39;</span><span class="p">,</span>
<a name="cl-1204"></a>                            <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.roleReference+json;ver=0.9&#39;</span>
<a name="cl-1205"></a>                    <span class="p">};</span>
<a name="cl-1206"></a>        
<a name="cl-1207"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1208"></a>                <span class="nx">url</span>      <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1209"></a>                <span class="nx">method</span>   <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-1210"></a>                <span class="nx">jsonData</span> <span class="o">:</span> <span class="nx">roledata</span><span class="p">,</span>
<a name="cl-1211"></a>                <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1212"></a>                <span class="nx">success</span>  <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1213"></a>                        <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1214"></a>                <span class="p">}</span>
<a name="cl-1215"></a>        <span class="p">});</span>
<a name="cl-1216"></a>        <span class="p">};</span>
<a name="cl-1217"></a>
<a name="cl-1218"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">removeRoleFromUser</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">userid</span><span class="p">,</span> <span class="nx">roleid</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1219"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/users/&#39;</span> <span class="o">+</span> <span class="nx">userid</span> <span class="o">+</span> <span class="s1">&#39;/roles/&#39;</span> <span class="o">+</span> <span class="nx">roleid</span><span class="p">;</span>
<a name="cl-1220"></a>            
<a name="cl-1221"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1222"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1223"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-1224"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1225"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1226"></a>            <span class="p">}</span>
<a name="cl-1227"></a>        <span class="p">});</span>
<a name="cl-1228"></a>        <span class="p">};</span>
<a name="cl-1229"></a>
<a name="cl-1230"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">removeRoleFromGroup</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">groupid</span><span class="p">,</span> <span class="nx">roleid</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
<a name="cl-1231"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/user/realm/groups/&#39;</span> <span class="o">+</span> <span class="nx">groupid</span> <span class="o">+</span> <span class="s1">&#39;/roles/&#39;</span> <span class="o">+</span> <span class="nx">roleid</span><span class="p">;</span>
<a name="cl-1232"></a>        
<a name="cl-1233"></a>                <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1234"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1235"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-1236"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1237"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1238"></a>            <span class="p">}</span>
<a name="cl-1239"></a>        <span class="p">});</span>
<a name="cl-1240"></a>        <span class="p">};</span>
<a name="cl-1241"></a>
<a name="cl-1242"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getProxy</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1243"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">buildUrl</span><span class="p">(</span><span class="s1">&#39;/user/realm/users&#39;</span><span class="p">),</span>                              
<a name="cl-1244"></a>                        <span class="nx">config</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1245"></a>                                <span class="nx">type</span>     <span class="o">:</span> <span class="s1">&#39;rest&#39;</span><span class="p">,</span>
<a name="cl-1246"></a>                        <span class="nx">url</span>      <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1247"></a>                                <span class="nx">pageParam</span>   <span class="o">:</span> <span class="s1">&#39;currentPage&#39;</span><span class="p">,</span>
<a name="cl-1248"></a>                                <span class="nx">limitParam</span>  <span class="o">:</span> <span class="s1">&#39;pageSize&#39;</span><span class="p">,</span>
<a name="cl-1249"></a>                                <span class="nx">startParam</span>  <span class="o">:</span> <span class="kc">null</span><span class="p">,</span>
<a name="cl-1250"></a>                                <span class="nx">headers</span>         <span class="o">:</span> <span class="nx">o</span><span class="p">.</span><span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-1251"></a>                                <span class="nx">reader</span>      <span class="o">:</span> <span class="p">{</span>
<a name="cl-1252"></a>                                        <span class="nx">type</span>    <span class="o">:</span> <span class="s1">&#39;json&#39;</span><span class="p">,</span>
<a name="cl-1253"></a>                                        <span class="nx">root</span>    <span class="o">:</span> <span class="s1">&#39;users&#39;</span><span class="p">,</span>
<a name="cl-1254"></a>                                        <span class="nx">totalProperty</span> <span class="o">:</span> <span class="s1">&#39;total&#39;</span><span class="p">,</span>
<a name="cl-1255"></a>                                        <span class="nx">getResponseData</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">response</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1256"></a>                                                <span class="kd">var</span> <span class="nx">data</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">self</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">getResponseData</span><span class="p">(</span><span class="nx">response</span><span class="p">);</span>
<a name="cl-1257"></a>                                                <span class="k">if</span> <span class="p">(</span><span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1258"></a>                                                        <span class="nx">data</span><span class="p">[</span><span class="s1">&#39;total&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">pageSize</span> <span class="o">*</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">totalPages</span><span class="p">;</span>
<a name="cl-1259"></a>                                                <span class="p">}</span>
<a name="cl-1260"></a>                                                <span class="k">return</span> <span class="nx">data</span><span class="p">;</span>
<a name="cl-1261"></a>                                        <span class="p">}</span>
<a name="cl-1262"></a>                                <span class="p">}</span>                                               
<a name="cl-1263"></a>                        <span class="p">};</span>
<a name="cl-1264"></a>                <span class="k">return</span> <span class="nx">config</span><span class="p">;</span>
<a name="cl-1265"></a>        <span class="p">};</span>
<a name="cl-1266"></a>
<a name="cl-1267"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getGroupProxy</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1268"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">buildUrl</span><span class="p">(</span><span class="s1">&#39;/user/realm/groups&#39;</span><span class="p">),</span>                             
<a name="cl-1269"></a>                        <span class="nx">config</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1270"></a>                                <span class="nx">type</span>     <span class="o">:</span> <span class="s1">&#39;rest&#39;</span><span class="p">,</span>
<a name="cl-1271"></a>                        <span class="nx">url</span>      <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1272"></a>                                <span class="nx">pageParam</span>   <span class="o">:</span> <span class="s1">&#39;currentPage&#39;</span><span class="p">,</span>
<a name="cl-1273"></a>                                <span class="nx">limitParam</span>  <span class="o">:</span> <span class="s1">&#39;pageSize&#39;</span><span class="p">,</span>
<a name="cl-1274"></a>                                <span class="nx">startParam</span>  <span class="o">:</span> <span class="kc">null</span><span class="p">,</span>
<a name="cl-1275"></a>                                <span class="nx">headers</span>         <span class="o">:</span> <span class="nx">o</span><span class="p">.</span><span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-1276"></a>                                <span class="nx">reader</span>      <span class="o">:</span> <span class="p">{</span>
<a name="cl-1277"></a>                                        <span class="nx">type</span>    <span class="o">:</span> <span class="s1">&#39;json&#39;</span><span class="p">,</span>
<a name="cl-1278"></a>                                        <span class="nx">root</span>    <span class="o">:</span> <span class="s1">&#39;groups&#39;</span><span class="p">,</span>
<a name="cl-1279"></a>                                        <span class="nx">totalProperty</span> <span class="o">:</span> <span class="s1">&#39;total&#39;</span><span class="p">,</span>
<a name="cl-1280"></a>                                        <span class="nx">getResponseData</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">response</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1281"></a>                                                <span class="kd">var</span> <span class="nx">data</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">self</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">getResponseData</span><span class="p">(</span><span class="nx">response</span><span class="p">);</span>
<a name="cl-1282"></a>                                                <span class="k">if</span> <span class="p">(</span><span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1283"></a>                                                        <span class="nx">data</span><span class="p">[</span><span class="s1">&#39;total&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">pageSize</span> <span class="o">*</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">totalPages</span><span class="p">;</span>
<a name="cl-1284"></a>                                                <span class="p">}</span>
<a name="cl-1285"></a>                                                <span class="k">return</span> <span class="nx">data</span><span class="p">;</span>
<a name="cl-1286"></a>                                        <span class="p">}</span>
<a name="cl-1287"></a>                                <span class="p">}</span>                                               
<a name="cl-1288"></a>                        <span class="p">};</span>
<a name="cl-1289"></a>                <span class="k">return</span> <span class="nx">config</span><span class="p">;</span>
<a name="cl-1290"></a>        <span class="p">};</span>
<a name="cl-1291"></a>
<a name="cl-1292"></a>        <span class="nx">o</span><span class="p">.</span><span class="nx">getRoleProxy</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1293"></a>                <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="nx">o</span><span class="p">.</span><span class="nx">buildUrl</span><span class="p">(</span><span class="s1">&#39;/user/roles&#39;</span><span class="p">),</span>                            
<a name="cl-1294"></a>                        <span class="nx">config</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1295"></a>                                <span class="nx">type</span>     <span class="o">:</span> <span class="s1">&#39;rest&#39;</span><span class="p">,</span>
<a name="cl-1296"></a>                        <span class="nx">url</span>      <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1297"></a>                                <span class="nx">pageParam</span>   <span class="o">:</span> <span class="s1">&#39;currentPage&#39;</span><span class="p">,</span>
<a name="cl-1298"></a>                                <span class="nx">limitParam</span>  <span class="o">:</span> <span class="s1">&#39;pageSize&#39;</span><span class="p">,</span>
<a name="cl-1299"></a>                                <span class="nx">startParam</span>  <span class="o">:</span> <span class="kc">null</span><span class="p">,</span>
<a name="cl-1300"></a>                                <span class="nx">headers</span>         <span class="o">:</span> <span class="nx">o</span><span class="p">.</span><span class="nx">defaults</span><span class="p">.</span><span class="nx">headers</span><span class="p">,</span>
<a name="cl-1301"></a>                                <span class="nx">reader</span>      <span class="o">:</span> <span class="p">{</span>
<a name="cl-1302"></a>                                        <span class="nx">type</span>    <span class="o">:</span> <span class="s1">&#39;json&#39;</span><span class="p">,</span>
<a name="cl-1303"></a>                                        <span class="nx">root</span>    <span class="o">:</span> <span class="s1">&#39;roles&#39;</span><span class="p">,</span>
<a name="cl-1304"></a>                                        <span class="nx">totalProperty</span> <span class="o">:</span> <span class="s1">&#39;total&#39;</span><span class="p">,</span>
<a name="cl-1305"></a>                                        <span class="nx">getResponseData</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">response</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1306"></a>                                                <span class="kd">var</span> <span class="nx">data</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">self</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">getResponseData</span><span class="p">(</span><span class="nx">response</span><span class="p">);</span>
<a name="cl-1307"></a>                                                <span class="k">if</span> <span class="p">(</span><span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1308"></a>                                                        <span class="nx">data</span><span class="p">[</span><span class="s1">&#39;total&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">pageSize</span> <span class="o">*</span> <span class="nx">data</span><span class="p">.</span><span class="nx">statistics</span><span class="p">.</span><span class="nx">totalPages</span><span class="p">;</span>
<a name="cl-1309"></a>                                                <span class="p">}</span>
<a name="cl-1310"></a>                                                <span class="k">return</span> <span class="nx">data</span><span class="p">;</span>
<a name="cl-1311"></a>                                        <span class="p">}</span>
<a name="cl-1312"></a>                                <span class="p">}</span>                                               
<a name="cl-1313"></a>                        <span class="p">};</span>
<a name="cl-1314"></a>                <span class="k">return</span> <span class="nx">config</span><span class="p">;</span>
<a name="cl-1315"></a>        <span class="p">};</span>
<a name="cl-1316"></a>            
<a name="cl-1317"></a>        <span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s1">&#39;user&#39;</span><span class="p">,</span> <span class="nx">o</span><span class="p">);</span>
<a name="cl-1318"></a><span class="p">})();</span>
<a name="cl-1319"></a><span class="cm">/**</span>
<a name="cl-1320"></a><span class="cm"> * @class C8Y.client.measurement</span>
<a name="cl-1321"></a><span class="cm"> * Measurement class, implements the measurement API methods</span>
<a name="cl-1322"></a><span class="cm"> * @singleton</span>
<a name="cl-1323"></a><span class="cm"> */</span>
<a name="cl-1324"></a><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1325"></a>    <span class="kd">var</span> <span class="nx">o</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-1326"></a>        <span class="nx">props</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-1327"></a>        <span class="nx">proxymodels</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-1328"></a>        <span class="cm">/**</span>
<a name="cl-1329"></a><span class="cm">         * Return a tenant Object</span>
<a name="cl-1330"></a><span class="cm">         * @return {String} tenant, the tenant object</span>
<a name="cl-1331"></a><span class="cm">         * @method</span>
<a name="cl-1332"></a><span class="cm">         */</span>    
<a name="cl-1333"></a>    <span class="kd">function</span> <span class="nx">getTenant</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1334"></a>        <span class="kd">var</span> <span class="nx">tenant</span><span class="p">;</span>
<a name="cl-1335"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span> <span class="o">||</span> <span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1336"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;getTenant() not defined, please check the auth module.&#39;</span><span class="p">);</span>
<a name="cl-1337"></a>        <span class="p">}</span>
<a name="cl-1338"></a>        <span class="k">return</span>  <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">();</span>
<a name="cl-1339"></a>    <span class="p">}</span>
<a name="cl-1340"></a>
<a name="cl-1341"></a>   <span class="cm">/**</span>
<a name="cl-1342"></a><span class="cm">        * Returns a collection of Mesaurementes</span>
<a name="cl-1343"></a><span class="cm">        * @param {Object} filters, an object with the following properties</span>
<a name="cl-1344"></a><span class="cm">        *       - pageSize</span>
<a name="cl-1345"></a><span class="cm">        *       - currentPage</span>
<a name="cl-1346"></a><span class="cm">        *       - dateFrom</span>
<a name="cl-1347"></a><span class="cm">        *       - dateTo</span>
<a name="cl-1348"></a><span class="cm">        *   - source</span>
<a name="cl-1349"></a><span class="cm">        *   - type</span>
<a name="cl-1350"></a><span class="cm">        * @param {int} pageSize, the number of items per page</span>
<a name="cl-1351"></a><span class="cm">        * @param {int} currentPage, the current page to display</span>
<a name="cl-1352"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1353"></a><span class="cm">        * @return {Object} ajax, the ajax response object with a collection of managed objects.</span>
<a name="cl-1354"></a><span class="cm">        * @method</span>
<a name="cl-1355"></a><span class="cm">        */</span>    
<a name="cl-1356"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">list</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">filters</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1357"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/measurement/measurements&#39;</span><span class="p">,</span>
<a name="cl-1358"></a>                        <span class="nx">params</span> <span class="o">=</span> <span class="nx">filters</span><span class="p">,</span>
<a name="cl-1359"></a>                        <span class="nx">defaultPageSize</span> <span class="o">=</span> <span class="mi">300</span><span class="p">,</span>
<a name="cl-1360"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1361"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.measurementCollection+json;ver=0.9&#39;</span>
<a name="cl-1362"></a>            <span class="p">};</span>
<a name="cl-1363"></a>                
<a name="cl-1364"></a>                <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">params</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1365"></a>                        <span class="nx">params</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-1366"></a>                <span class="p">}</span>
<a name="cl-1367"></a>                <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;pageSize&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;pageSize&#39;</span><span class="p">]</span> <span class="o">||</span> <span class="nx">defaultPageSize</span><span class="p">;</span>
<a name="cl-1368"></a>                <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;currentPage&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;currentPage&#39;</span><span class="p">]</span> <span class="o">||</span> <span class="mi">1</span><span class="p">;</span>
<a name="cl-1369"></a>                
<a name="cl-1370"></a>                <span class="c1">// params[&#39;dateFrom&#39;] = new Date(&#39;2011-11-01&#39;);</span>
<a name="cl-1371"></a>                <span class="c1">//      params[&#39;dateTo&#39;] = new Date(&#39;2011-11-18&#39;);</span>
<a name="cl-1372"></a>                <span class="c1">//      params[&#39;source&#39;] = 12;</span>
<a name="cl-1373"></a>
<a name="cl-1374"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1375"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1376"></a>            <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1377"></a>                        <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-1378"></a>                        <span class="nx">params</span>  <span class="o">:</span> <span class="nx">params</span><span class="p">,</span>
<a name="cl-1379"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1380"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1381"></a>            <span class="p">}</span>
<a name="cl-1382"></a>        <span class="p">});</span>
<a name="cl-1383"></a>    <span class="p">};</span>
<a name="cl-1384"></a>    
<a name="cl-1385"></a>   <span class="cm">/**</span>
<a name="cl-1386"></a><span class="cm">        * Returns the managed object for the passed identifier</span>
<a name="cl-1387"></a><span class="cm">        * @param {String} id, the managed object identifier</span>
<a name="cl-1388"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1389"></a><span class="cm">        * @return {Object} ajax the ajax response with the managed object for the passed id</span>
<a name="cl-1390"></a><span class="cm">        * @method</span>
<a name="cl-1391"></a><span class="cm">        */</span>    
<a name="cl-1392"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">get</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1393"></a>        
<a name="cl-1394"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-1395"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/inventory/managedObjects/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1396"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1397"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.measurement+json;ver=0.9&#39;</span>
<a name="cl-1398"></a>            <span class="p">};</span>
<a name="cl-1399"></a>        
<a name="cl-1400"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1401"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1402"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-1403"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1404"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1405"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1406"></a>            <span class="p">}</span>
<a name="cl-1407"></a>        <span class="p">});</span>
<a name="cl-1408"></a>    <span class="p">};</span>
<a name="cl-1409"></a>
<a name="cl-1410"></a>    
<a name="cl-1411"></a>   <span class="cm">/**</span>
<a name="cl-1412"></a><span class="cm">        * Returns 204 deleted reference</span>
<a name="cl-1413"></a><span class="cm">        * @param {String} id, the identifier of the measurement</span>
<a name="cl-1414"></a><span class="cm">        * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1415"></a><span class="cm">        * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1416"></a><span class="cm">        * @method</span>
<a name="cl-1417"></a><span class="cm">        */</span>    
<a name="cl-1418"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">remove</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1419"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/measurement/measurements/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">;</span>
<a name="cl-1420"></a>        
<a name="cl-1421"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1422"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1423"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;DELETE&#39;</span><span class="p">,</span>
<a name="cl-1424"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1425"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1426"></a>            <span class="p">}</span>
<a name="cl-1427"></a>        <span class="p">});</span>
<a name="cl-1428"></a>    <span class="p">};</span>  
<a name="cl-1429"></a>   
<a name="cl-1430"></a>    <span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s1">&#39;measurement&#39;</span><span class="p">,</span> <span class="nx">o</span><span class="p">);</span>
<a name="cl-1431"></a><span class="p">})();</span>
<a name="cl-1432"></a><span class="cm">/**</span>
<a name="cl-1433"></a><span class="cm"> * @class C8Y.client.devicecontrol</span>
<a name="cl-1434"></a><span class="cm"> * Device control class, implements the device control API methods</span>
<a name="cl-1435"></a><span class="cm"> * @singleton</span>
<a name="cl-1436"></a><span class="cm"> */</span>
<a name="cl-1437"></a><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1438"></a>    <span class="kd">var</span> <span class="nx">o</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-1439"></a>        <span class="nx">props</span> <span class="o">=</span> <span class="p">{},</span>
<a name="cl-1440"></a>        <span class="nx">proxymodels</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-1441"></a>    <span class="cm">/**</span>
<a name="cl-1442"></a><span class="cm">     * Return a tenant Object</span>
<a name="cl-1443"></a><span class="cm">     * @return {String} tenant, the tenant object</span>
<a name="cl-1444"></a><span class="cm">     * @method</span>
<a name="cl-1445"></a><span class="cm">     */</span>    
<a name="cl-1446"></a>    <span class="kd">function</span> <span class="nx">getTenant</span><span class="p">()</span> <span class="p">{</span>
<a name="cl-1447"></a>        <span class="kd">var</span> <span class="nx">tenant</span><span class="p">;</span>
<a name="cl-1448"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span> <span class="o">||</span> <span class="o">!</span><span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1449"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;getTenant() not defined, please check the auth module.&#39;</span><span class="p">);</span>
<a name="cl-1450"></a>        <span class="p">}</span>
<a name="cl-1451"></a>        <span class="k">return</span>  <span class="nx">o</span><span class="p">.</span><span class="nx">output</span><span class="p">.</span><span class="nx">getTenant</span><span class="p">();</span>
<a name="cl-1452"></a>    <span class="p">}</span>
<a name="cl-1453"></a>
<a name="cl-1454"></a>   <span class="cm">/**</span>
<a name="cl-1455"></a><span class="cm">    * Returns a collection of Operations</span>
<a name="cl-1456"></a><span class="cm">    * @param {Object} filters, an object with the following properties</span>
<a name="cl-1457"></a><span class="cm">    *   - status</span>
<a name="cl-1458"></a><span class="cm">    *   - agentId</span>
<a name="cl-1459"></a><span class="cm">    *   - deviceId</span>
<a name="cl-1460"></a><span class="cm">    *   - pageSize</span>
<a name="cl-1461"></a><span class="cm">    *   - currentPage</span>
<a name="cl-1462"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1463"></a><span class="cm">    * @return {Object} ajax, the ajax response object with a collection of managed objects.</span>
<a name="cl-1464"></a><span class="cm">    * @method</span>
<a name="cl-1465"></a><span class="cm">    */</span>    
<a name="cl-1466"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">list</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">filters</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1467"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/devicecontrol/operations&#39;</span><span class="p">,</span>
<a name="cl-1468"></a>            <span class="nx">params</span> <span class="o">=</span> <span class="nx">filters</span><span class="p">,</span>
<a name="cl-1469"></a>            <span class="nx">defaultPageSize</span> <span class="o">=</span> <span class="mi">300</span><span class="p">,</span>
<a name="cl-1470"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1471"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.operationCollection+json;ver=0.9&#39;</span>
<a name="cl-1472"></a>            <span class="p">};</span>
<a name="cl-1473"></a>        
<a name="cl-1474"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">params</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1475"></a>            <span class="nx">params</span> <span class="o">=</span> <span class="p">{};</span>
<a name="cl-1476"></a>        <span class="p">}</span>
<a name="cl-1477"></a>        <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;pageSize&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;pageSize&#39;</span><span class="p">]</span> <span class="o">||</span> <span class="nx">defaultPageSize</span><span class="p">;</span>
<a name="cl-1478"></a>        <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;currentPage&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">params</span><span class="p">[</span><span class="s1">&#39;currentPage&#39;</span><span class="p">]</span> <span class="o">||</span> <span class="mi">1</span><span class="p">;</span>
<a name="cl-1479"></a>        
<a name="cl-1480"></a>        <span class="c1">// params[&#39;dateFrom&#39;] = new Date(&#39;2011-11-01&#39;);</span>
<a name="cl-1481"></a>        <span class="c1">//  params[&#39;dateTo&#39;] = new Date(&#39;2011-11-18&#39;);</span>
<a name="cl-1482"></a>        <span class="c1">//  params[&#39;source&#39;] = 12;</span>
<a name="cl-1483"></a>
<a name="cl-1484"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1485"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1486"></a>            <span class="nx">headers</span>  <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1487"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-1488"></a>            <span class="nx">params</span>  <span class="o">:</span> <span class="nx">params</span><span class="p">,</span>
<a name="cl-1489"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1490"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1491"></a>            <span class="p">}</span>
<a name="cl-1492"></a>        <span class="p">});</span>
<a name="cl-1493"></a>    <span class="p">};</span>
<a name="cl-1494"></a>    
<a name="cl-1495"></a>   <span class="cm">/**</span>
<a name="cl-1496"></a><span class="cm">    * Returns the operation with the called id</span>
<a name="cl-1497"></a><span class="cm">    * @param {String} id, the operation identifier</span>
<a name="cl-1498"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1499"></a><span class="cm">    * @return {Object} ajax the ajax response with the managed object for the passed id</span>
<a name="cl-1500"></a><span class="cm">    * @method</span>
<a name="cl-1501"></a><span class="cm">    */</span>    
<a name="cl-1502"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">get</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1503"></a>        
<a name="cl-1504"></a>        <span class="kd">var</span> <span class="nx">tenant</span> <span class="o">=</span> <span class="nx">getTenant</span><span class="p">(),</span>
<a name="cl-1505"></a>            <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/devicecontrol/operations/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1506"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1507"></a>                <span class="s1">&#39;Accept&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.operation+json;ver=0.9&#39;</span>
<a name="cl-1508"></a>            <span class="p">};</span>
<a name="cl-1509"></a>        
<a name="cl-1510"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1511"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1512"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;GET&#39;</span><span class="p">,</span>
<a name="cl-1513"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1514"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1515"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1516"></a>            <span class="p">}</span>
<a name="cl-1517"></a>        <span class="p">});</span>
<a name="cl-1518"></a>    <span class="p">};</span>
<a name="cl-1519"></a>
<a name="cl-1520"></a>    <span class="cm">/**</span>
<a name="cl-1521"></a><span class="cm">    * Creates an operation</span>
<a name="cl-1522"></a><span class="cm">    * @param {Object} data, the operation object to be created</span>
<a name="cl-1523"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1524"></a><span class="cm">    * @return {Object} ajax, the ajax response with the managed object itself</span>
<a name="cl-1525"></a><span class="cm">    * @method</span>
<a name="cl-1526"></a><span class="cm">    */</span>
<a name="cl-1527"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">create</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">deviceid</span><span class="p">,</span> <span class="nx">data</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1528"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/devicecontrol/operations&#39;</span><span class="p">,</span>
<a name="cl-1529"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1530"></a>                <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.operation+json;ver=0.9&#39;</span>
<a name="cl-1531"></a>            <span class="p">};</span>
<a name="cl-1532"></a>        
<a name="cl-1533"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">data</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1534"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;Operation data not defined&#39;</span><span class="p">);</span>
<a name="cl-1535"></a>        <span class="p">}</span>
<a name="cl-1536"></a>        
<a name="cl-1537"></a>        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">deviceid</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1538"></a>            <span class="k">throw</span><span class="p">(</span><span class="s1">&#39;Device Id not defined&#39;</span><span class="p">);</span>
<a name="cl-1539"></a>        <span class="p">}</span>
<a name="cl-1540"></a>        
<a name="cl-1541"></a>        <span class="nx">data</span><span class="p">[</span><span class="s1">&#39;deviceId&#39;</span><span class="p">]</span> <span class="o">=</span> <span class="nx">deviceid</span><span class="p">;</span>
<a name="cl-1542"></a>
<a name="cl-1543"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1544"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1545"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;POST&#39;</span><span class="p">,</span>
<a name="cl-1546"></a>            <span class="nx">headers</span> <span class="o">:</span> <span class="nx">headers</span><span class="p">,</span>
<a name="cl-1547"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-1548"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1549"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1550"></a>            <span class="p">}</span>
<a name="cl-1551"></a>        <span class="p">});</span>
<a name="cl-1552"></a>    <span class="p">};</span>
<a name="cl-1553"></a>
<a name="cl-1554"></a>    
<a name="cl-1555"></a>   <span class="cm">/**</span>
<a name="cl-1556"></a><span class="cm">    * Changes the status of the referenced operation</span>
<a name="cl-1557"></a><span class="cm">    * @param {String} id, the identifier of the operation</span>
<a name="cl-1558"></a><span class="cm">    * @param {String} status, the string for the status SUCCESSFUL, FAILED, EXECUTING or PENDING</span>
<a name="cl-1559"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1560"></a><span class="cm">    * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1561"></a><span class="cm">    * @method</span>
<a name="cl-1562"></a><span class="cm">    */</span>    
<a name="cl-1563"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">changeStatus</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">status</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1564"></a>        <span class="kd">var</span> <span class="nx">url</span> <span class="o">=</span> <span class="s1">&#39;/devicecontrol/operations/&#39;</span><span class="o">+</span><span class="nx">id</span><span class="p">,</span>
<a name="cl-1565"></a>            <span class="nx">headers</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1566"></a>                <span class="s1">&#39;Content-Type&#39;</span> <span class="o">:</span> <span class="s1">&#39;application/vnd.com.nsn.cumulocity.operation+json;ver=0.9&#39;</span>
<a name="cl-1567"></a>            <span class="p">},</span>
<a name="cl-1568"></a>            <span class="nx">data</span> <span class="o">=</span> <span class="p">{</span>
<a name="cl-1569"></a>                <span class="nx">status</span> <span class="o">:</span> <span class="nx">status</span>
<a name="cl-1570"></a>            <span class="p">};</span>
<a name="cl-1571"></a>        
<a name="cl-1572"></a>        <span class="k">return</span> <span class="nx">o</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
<a name="cl-1573"></a>            <span class="nx">url</span>     <span class="o">:</span> <span class="nx">url</span><span class="p">,</span>
<a name="cl-1574"></a>            <span class="nx">method</span>  <span class="o">:</span> <span class="s1">&#39;PUT&#39;</span><span class="p">,</span>
<a name="cl-1575"></a>            <span class="nx">jsonData</span><span class="o">:</span> <span class="nx">data</span><span class="p">,</span>
<a name="cl-1576"></a>            <span class="nx">success</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1577"></a>                <span class="nx">callback</span> <span class="o">&amp;&amp;</span> <span class="nx">callback</span><span class="p">(</span><span class="nx">r</span><span class="p">);</span>
<a name="cl-1578"></a>            <span class="p">}</span>
<a name="cl-1579"></a>        <span class="p">});</span>
<a name="cl-1580"></a>    <span class="p">};</span>
<a name="cl-1581"></a>
<a name="cl-1582"></a>    <span class="cm">/**</span>
<a name="cl-1583"></a><span class="cm">    * Change the operation status to FAILED</span>
<a name="cl-1584"></a><span class="cm">    * @param {String} id, the identifier of the operation</span>
<a name="cl-1585"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1586"></a><span class="cm">    * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1587"></a><span class="cm">    * @method</span>
<a name="cl-1588"></a><span class="cm">    */</span>    
<a name="cl-1589"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">fail</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1590"></a>       <span class="nx">o</span><span class="p">.</span><span class="nx">changeStatus</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span><span class="s1">&#39;FAILED&#39;</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-1591"></a>    <span class="p">};</span>
<a name="cl-1592"></a>
<a name="cl-1593"></a>    <span class="cm">/**</span>
<a name="cl-1594"></a><span class="cm">    * Change the operation status to SUCCESSFUL</span>
<a name="cl-1595"></a><span class="cm">    * @param {String} id, the identifier of the operation</span>
<a name="cl-1596"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1597"></a><span class="cm">    * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1598"></a><span class="cm">    * @method</span>
<a name="cl-1599"></a><span class="cm">    */</span>    
<a name="cl-1600"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">success</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1601"></a>       <span class="nx">o</span><span class="p">.</span><span class="nx">changeStatus</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span><span class="s1">&#39;SUCCESSFUL&#39;</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-1602"></a>    <span class="p">};</span>
<a name="cl-1603"></a>
<a name="cl-1604"></a>    <span class="cm">/**</span>
<a name="cl-1605"></a><span class="cm">    * Change the operation status to SUCCESSFUL</span>
<a name="cl-1606"></a><span class="cm">    * @param {String} id, the identifier of the operation</span>
<a name="cl-1607"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1608"></a><span class="cm">    * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1609"></a><span class="cm">    * @method</span>
<a name="cl-1610"></a><span class="cm">    */</span>    
<a name="cl-1611"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">execute</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1612"></a>       <span class="nx">o</span><span class="p">.</span><span class="nx">changeStatus</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span><span class="s1">&#39;EXECUTING&#39;</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-1613"></a>    <span class="p">};</span>
<a name="cl-1614"></a>
<a name="cl-1615"></a>    <span class="cm">/**</span>
<a name="cl-1616"></a><span class="cm">    * Change the operation status to SUCCESSFUL</span>
<a name="cl-1617"></a><span class="cm">    * @param {String} id, the identifier of the operation</span>
<a name="cl-1618"></a><span class="cm">    * @param {Function} callback, the callback function to be executed upon ajax response</span>
<a name="cl-1619"></a><span class="cm">    * @return {Object} ajax, the ajax response object with the managed object reference</span>
<a name="cl-1620"></a><span class="cm">    * @method</span>
<a name="cl-1621"></a><span class="cm">    */</span>    
<a name="cl-1622"></a>    <span class="nx">o</span><span class="p">.</span><span class="nx">pending</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span> <span class="nx">callback</span><span class="p">)</span> <span class="p">{</span>
<a name="cl-1623"></a>       <span class="nx">o</span><span class="p">.</span><span class="nx">changeStatus</span><span class="p">(</span><span class="nx">id</span><span class="p">,</span><span class="s1">&#39;PENDING&#39;</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
<a name="cl-1624"></a>    <span class="p">};</span>
<a name="cl-1625"></a>
<a name="cl-1626"></a>    
<a name="cl-1627"></a>   
<a name="cl-1628"></a>    <span class="nx">C8Y</span><span class="p">.</span><span class="nx">client</span><span class="p">.</span><span class="nx">add</span><span class="p">(</span><span class="s1">&#39;devicecontrol&#39;</span><span class="p">,</span> <span class="nx">o</span><span class="p">);</span>
<a name="cl-1629"></a><span class="p">})();</span>
</pre></div>
</td></tr></table>
    </div>
  
  </div>
  


  <div id="mask"><div></div></div>

  </div>

      </div>
    </div>

  </div>

  <div id="footer">
    <ul id="footer-nav">
      <li>Copyright © 2011 <a href="http://atlassian.com">Atlassian</a></li>
      <li><a href="http://www.atlassian.com/hosted/terms.jsp">Terms of Service</a></li>
      <li><a href="http://www.atlassian.com/about/privacy.jsp">Privacy</a></li>
      <li><a href="//bitbucket.org/site/master/issues/new">Report a Bug to Bitbucket</a></li>
      <li><a href="http://confluence.atlassian.com/x/IYBGDQ">API</a></li>
      <li><a href="http://status.bitbucket.org/">Server Status</a></li>
    </ul>
    <ul id="social-nav">
      <li class="blog"><a href="http://blog.bitbucket.org">Bitbucket Blog</a></li>
      <li class="twitter"><a href="http://www.twitter.com/bitbucket">Twitter</a></li>
    </ul>
    <h5>We run</h5>
    <ul id="technologies">
      <li><a href="http://www.djangoproject.com/">Django 1.3.1</a></li>
      <li><a href="//bitbucket.org/jespern/django-piston/">Piston 0.3dev</a></li>
      <li><a href="http://git-scm.com/">Git 1.7.6</a></li>
      <li><a href="http://www.selenic.com/mercurial/">Hg 1.9.2</a></li>
      <li><a href="http://www.python.org">Python 2.7.2</a></li>
      <li>01bd985291d3 | bitbucket12</li>
    </ul>
  </div>

  <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/js/lib/global.js"></script>






  <script>
    BB.gaqPush(['_trackPageview']);
  
    BB.gaqPush(['atl._trackPageview']);

    

    

    (function () {
        var ga = document.createElement('script');
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        ga.setAttribute('async', 'true');
        document.documentElement.firstChild.appendChild(ga);
    }());
  </script>

</body>
</html>
